package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.user.UserBasicInfo;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;


@RequiredArgsConstructor
public class GetUsersByEmailsUseCase implements GetUsersByEmail {
    private final UserRepository userRepository;
    private final LoggerPort logger;

    public Flux<UserBasicInfo> apply(List<String> emails) {
        logger.info("Buscando usuarios por emails: {}", emails);
        return userRepository.findByEmails(emails)
                .doOnNext(user -> logger.info("Usuario encontrado: {}", user.getEmail()));
    }
}