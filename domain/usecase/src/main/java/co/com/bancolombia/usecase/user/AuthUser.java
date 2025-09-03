package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.records.Token;
import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthUser {
    Mono<Token> apply(String user, String rawPassword);
}
