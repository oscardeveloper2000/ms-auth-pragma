package co.com.bancolombia.api;

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
private final CreateUserUseCase userUseCase;
    private final Validator validator;
//private  final UseCase2 useCase2;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        // useCase.logic();
        return serverRequest.bodyToMono(User.class)
                .flatMap(userUseCase::createUser)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }
    public Mono<ServerResponse> listenSaveUserV(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user -> {
                    var violations = validator.validate(user);
                    if (!violations.isEmpty()) {
                        String errorMsg = violations.stream()
                                .map(v -> v.getMessage())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("Datos inválidos");
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(errorMsg);
                    }
                    return userUseCase.createUser(user)
                            .flatMap(saved -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(saved));
                });
    }
}
