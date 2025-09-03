package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.records.Token;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.AuthGateway;
import co.com.bancolombia.model.user.gateways.EncryptPasswordGateway;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@RequiredArgsConstructor
public class AuthUserUseCase implements AuthUser {
    private final UserRepository userRepository;
    private final RoleRepository rolRepository;
    private final LoggerPort logger;
    private final EncryptPasswordGateway encryptPasswordGateway;
    private final AuthGateway authGateway;
    @Override
    public Mono<Token> apply(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(user -> rolRepository.findById(user.getRoleId())
                        .switchIfEmpty(Mono.error( new RuntimeException("Role not found " + user.getRoleId())))
                        .map( role -> user.toBuilder().roleName(role.getName()).build() ))
                .flatMap(user -> {
                    if (encryptPasswordGateway.checkPassword(rawPassword, user.getPasswordHash())) {
                        return Mono.fromSupplier(() -> {
                            String jwt = authGateway.generateToken(user);
                            return new Token(jwt);
                        });
                    } else {
                        return Mono.error(new RuntimeException("Credenciales inválidas"));
                    }
                });
    }
}
