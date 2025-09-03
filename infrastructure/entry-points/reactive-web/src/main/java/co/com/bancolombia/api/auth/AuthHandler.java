package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.auth.dto.AuthRestMapper;
import co.com.bancolombia.api.auth.dto.LoginRequest;

import co.com.bancolombia.api.error.ErrorHandler;
import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.usecase.user.AuthUser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Transactional
public class AuthHandler {
    private final AuthUser authUserUseCase;
    private final LoggerPort logger;
    private final ErrorHandler errorHandler;
    private final AuthRestMapper authRestMapper;

    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        logger.info("Iniciando login");
        return serverRequest.bodyToMono(LoginRequest.class)
                .map(authRestMapper::toLogin)
                .flatMap(login -> authUserUseCase.apply(login.email(), login.password()))
                .map(authRestMapper::toTokenResponse)
                .flatMap(tokenResponse -> {
                    logger.info("Login exitoso");
                    return ServerResponse.ok().bodyValue(tokenResponse);
                })
                .onErrorResume(error -> {
                    logger.error("Error en login: {}", error.getMessage());
                    return errorHandler.handle(error, serverRequest);
                });
    }
}
