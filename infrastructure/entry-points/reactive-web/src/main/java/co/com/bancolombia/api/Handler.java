package co.com.bancolombia.api;

import co.com.bancolombia.api.user.dto.UserMapper;
import co.com.bancolombia.api.user.dto.UserRecord;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.createuser.CreateUserUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
private final CreateUserUseCase createUseCase;
private final ValidatorHandler validatorHandler;
private final UserMapper userMapper;

public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
              return serverRequest.bodyToMono(User.class)
                      .flatMap(user -> createUseCase.apply(Mono.just(user)))
                      .flatMap(user -> ServerResponse.ok()
                              .contentType(MediaType.APPLICATION_JSON)
                              .bodyValue(user));
          }


    public Mono<ServerResponse> listenSaveUserV(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRecord.class)
                .flatMap(validatorHandler::validate)
                .map(userMapper::toModel)
                .flatMap(user -> createUseCase.apply(Mono.just(user)))
                .map(userMapper::toDTO)
                .flatMap(userRecord -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRecord))
                // .onErrorResume( e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                ;
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }


}
