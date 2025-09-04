package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.UserBasicInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByDocumentNumber(String documentNumber);
    Mono<User> findByEmail(String email);
    Flux<UserBasicInfo> findByEmails(List<String> emails);
}
