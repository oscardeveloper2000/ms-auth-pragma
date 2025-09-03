package co.com.bancolombia.api.user;

import co.com.bancolombia.api.error.ErrorHandler;
import co.com.bancolombia.api.user.dto.UserMapper;
import co.com.bancolombia.api.user.dto.UserRecord;
import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.usecase.user.CreateUser;
import co.com.bancolombia.usecase.user.GetUserByDocumentNumber;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;



@Component
@RequiredArgsConstructor
@Transactional
public class UserHandler {
    private final CreateUser createUseCase;
    private final GetUserByDocumentNumber getUserByDocumentNumberUseCase;
    private final UserMapper userMapper;
    private final LoggerPort logger;
    private final ErrorHandler errorHandler;


    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        logger.info("Iniciando guardado de usuario");
        return serverRequest.bodyToMono(UserRecord.class)
                .map(userMapper::toModel)
                .flatMap(user -> createUseCase.apply(Mono.just(user)))
                .map(userMapper::toDTO)
                .flatMap(userRecord -> {
                    logger.info("Usuario guardado exitosamente");
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userRecord);
                })
                .onErrorResume(error -> {
                    logger.error("Error al guardar usuario: {}", error.getMessage());
                    return errorHandler.handle(error, serverRequest);
                });
    }

    public Mono<ServerResponse> listenGetUserByDocumentNumber(ServerRequest request) {
        String documentNumber = request.pathVariable("documentNumber");
        logger.info("Buscando usuario con documento: {}", documentNumber);
        return getUserByDocumentNumberUseCase.apply(documentNumber)
                .map(userMapper::toDTO)
                .flatMap(dto -> {
                    logger.info("Usuario encontrado");
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(dto);
                })
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> {
                    logger.error("Error al buscar usuario: {}", error.getMessage());
                    return errorHandler.handle(error, request);
                });
    }
}

