package co.com.bancolombia.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancolombia.usecase.createuser.UserAlreadyExistsException;
import co.com.bancolombia.usecase.createuser.DomainValidationException;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

// ... existing code ...
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);
    private static final String CORRELATION_ID_HEADER = "correlationId";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(CORRELATION_ID_HEADER, correlationId);

        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String headerMessage = "Internal Server Error";
        String detailCode = "500";
        String detailMessage = "Ocurrió un error inesperado";

        if (ex instanceof ConstraintViolationException cve) {
            status = HttpStatus.BAD_REQUEST.value();
            headerMessage = "Bad Request";
            detailCode = "400";
            detailMessage = cve.getConstraintViolations().isEmpty()
                    ? cve.getMessage()
                    : cve.getConstraintViolations().stream()
                    .map(v -> {
                        String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
                        String msg = v.getMessage() != null ? v.getMessage() : "violación de restricción";
                        return path.isBlank() ? msg : (path + ": " + msg);
                    })
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof DomainValidationException) {
            status = HttpStatus.BAD_REQUEST.value();
            headerMessage = "Bad Request";
            detailCode = "400";
            detailMessage = ex.getMessage();
        } else if (ex instanceof UserAlreadyExistsException) {
            status = HttpStatus.CONFLICT.value(); // 409
            headerMessage = "Conflict";
            detailCode = "409";
            detailMessage = ex.getMessage();
        }


        // Log con contexto (no incluir cuerpos ni headers sensibles)
        org.springframework.http.server.reactive.ServerHttpRequest request = exchange.getRequest();
        java.net.URI requestUri = request.getURI();
        String httpMethod = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path = requestUri.getRawPath();
        String query = requestUri.getRawQuery();


        if (status >= 500) {
            log.error("Unhandled error: status={}, method={}, path={}{}{}, correlationId={}",
                    status, httpMethod, path, (query != null ? "?" : ""), (query != null ? query : ""), correlationId, ex);
        } else {
            log.warn("Client error: status={}, method={}, path={}{}{}, correlationId={}, message={}",
                    status, httpMethod, path, (query != null ? "?" : ""), (query != null ? query : ""), correlationId, detailMessage);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .correlationId(correlationId)
                .errorHeader(ErrorResponse.ErrorHeader.builder()
                        .returnCode(status)
                        .message(headerMessage)
                        .build())
                .errorDetail(ErrorResponse.ErrorDetail.builder()
                        .code(detailCode)
                        .message(detailMessage)
                        .errorDate(LocalDate.now().toString())
                        .build())
                .build();

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set(CORRELATION_ID_HEADER, correlationId);

        try {
            byte[] bytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer))
                    .doFinally(signal -> MDC.remove(CORRELATION_ID_HEADER));
        } catch (Exception e) {
            return exchange.getResponse().setComplete()
                    .doFinally(signal -> MDC.remove(CORRELATION_ID_HEADER));
        }
    }

}
