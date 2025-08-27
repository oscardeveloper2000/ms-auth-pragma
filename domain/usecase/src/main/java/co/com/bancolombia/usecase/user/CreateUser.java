package co.com.bancolombia.usecase.user;

import reactor.core.publisher.Mono;
import co.com.bancolombia.model.user.User;

public interface CreateUser {
    Mono<User> apply(Mono<User> user);
}
