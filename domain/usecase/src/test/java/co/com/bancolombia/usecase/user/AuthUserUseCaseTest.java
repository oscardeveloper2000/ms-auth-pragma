package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.AuthGateway;
import co.com.bancolombia.model.user.gateways.EncryptPasswordGateway;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class AuthUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EncryptPasswordGateway encryptPasswordGateway;
    @Mock
    private LoggerPort logger;
    @Mock
    private AuthGateway authGateway;

    private AuthUserUseCase authUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authUserUseCase = new AuthUserUseCase(userRepository, roleRepository, logger, encryptPasswordGateway, authGateway);
    }

    private User buildActiveUser() {
        return User.builder()
                .id(1L)
                .email("test@email.com")
                .passwordHash("hashedpass")
                .roleId(1L)
                .build();
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        User user = buildActiveUser();
        when(userRepository.findByEmail("test@email.com")).thenReturn(Mono.just(user));
        when(roleRepository.findById(user.getRoleId()))
                .thenReturn(Mono.just(co.com.bancolombia.model.role.Role.builder().id(user.getRoleId()).name("ADMIN").build()));
        when(encryptPasswordGateway.checkPassword("plainpass", "hashedpass")).thenReturn(true);
        when(authGateway.generateToken(any(User.class))).thenReturn("token-jwt");

        StepVerifier.create(authUserUseCase.apply("test@email.com", "plainpass"))
                .expectNextMatches(token -> token.token().equals("token-jwt"))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Mono.empty());

        StepVerifier.create(authUserUseCase.apply("notfound@email.com", "pass"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().contains("Usuario no encontrado"))
                .verify();
    }

    @Test
    void shouldFailWhenPasswordDoesNotMatch() {
        User user = buildActiveUser();
        when(userRepository.findByEmail("test@email.com")).thenReturn(Mono.just(user));
        when(roleRepository.findById(user.getRoleId()))
                .thenReturn(Mono.just(co.com.bancolombia.model.role.Role.builder().id(user.getRoleId()).name("ADMIN").build()));
        when(encryptPasswordGateway.checkPassword("wrongpass", "hashedpass")).thenReturn(false);

        StepVerifier.create(authUserUseCase.apply("test@email.com", "wrongpass"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().contains("Credenciales inválidas"))
                .verify();
    }

    @Test
    void shouldFailWhenRoleNotFound() {
        User user = buildActiveUser();
        when(userRepository.findByEmail("test@email.com")).thenReturn(Mono.just(user));
        when(roleRepository.findById(user.getRoleId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(authUserUseCase.apply("test@email.com", "plainpass"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().contains("Role not found"))
                .verify();
    }
}