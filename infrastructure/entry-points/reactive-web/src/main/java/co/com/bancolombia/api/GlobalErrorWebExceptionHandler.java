package co.com.bancolombia.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.bancolombia.usecase.createuser.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;


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

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String headerMessage = "Internal Server Error";
        String detailCode = "500";
        String detailMessage = "Ocurrió un error inesperado";

        if (ex instanceof ConstraintViolationException) {
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

        try {
            byte[] bytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

}
