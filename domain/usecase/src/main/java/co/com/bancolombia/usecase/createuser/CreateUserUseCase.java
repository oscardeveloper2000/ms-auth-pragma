package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if(exists) {
                        return Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado"));
                    }
                    return userRepository.save(user);
                });
    }
}
