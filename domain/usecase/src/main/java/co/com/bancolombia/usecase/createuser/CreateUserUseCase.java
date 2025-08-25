package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase implements CreateUser {

    private final UserRepository userRepository;


    @Override
    public Mono<User> apply(Mono<User> user) {
        return user.flatMap(userDTO ->
            userRepository.existsByEmail(userDTO.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UserAlreadyExistsException("Ya existe el usuario con correo " + userDTO.getEmail()));
                    }
                    return userRepository.save(userDTO);
                })
        );
    }
}
