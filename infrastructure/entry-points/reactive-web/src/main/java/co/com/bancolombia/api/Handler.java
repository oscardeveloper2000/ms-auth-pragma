package co.com.bancolombia.api;

import co.com.bancolombia.api.user.dto.UserMapper;
import co.com.bancolombia.api.user.dto.UserRecord;
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
public class Handler {
private final CreateUser createUseCase;
private final ValidatorHandler validatorHandler;
private final UserMapper userMapper;
    private final GetUserByDocumentNumber getUserByDocumentNumberUseCase;



    public Mono<ServerResponse> listenSaveUserV(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRecord.class)
                .flatMap(validatorHandler::validate)
                .map(userMapper::toModel)
                .flatMap(user -> createUseCase.apply(Mono.just(user)))
                .map(userMapper::toDTO)
                .flatMap(userRecord -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRecord))
//                 .onErrorResume( e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                ;
    }

    public Mono<ServerResponse> listenGetUserByDocumentNumber(ServerRequest request) {
        String documentNumber = request.pathVariable("documentNumber");
        return getUserByDocumentNumberUseCase.apply(documentNumber)
                .map(userMapper::toDTO)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }





}
