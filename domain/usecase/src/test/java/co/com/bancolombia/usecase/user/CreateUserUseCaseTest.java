package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoggerPort logger;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createUserUseCase = new CreateUserUseCase(userRepository, logger);
    }

    @Test
    void apply_ShouldCreateUserSuccessfully_WhenEmailNotRegistered() {
        User user = User.builder().email("test@example.com").baseSalary(new BigDecimal("5000")).build();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectNext(user)
                .verifyComplete();

        verify(logger).info("Iniciando creación de usuario");
        verify(logger).info("Usuario creado exitosamente: id={}, email={}", null, "t***t@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void apply_ShouldThrowException_WhenEmailAlreadyRegistered() {
        User user = User.builder().email("existing@example.com").baseSalary(new BigDecimal("5000")).build();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(logger).warn("Intento de creación con email ya existente: {}", "e***g@example.com");
        verify(userRepository, never()).save(user);
    }

//    @Test
//    void apply_ShouldThrowException_WhenEmailIsInvalid() {
//        User user = User.builder().email("invalid-email").baseSalary(new BigDecimal("5000")).build();
//
//        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
//                .expectError(DomainValidationException.class)
//                .verify();
//
//        verify(logger).error(any());
//        verify(userRepository, never()).existsByEmail(any());
//        verify(userRepository, never()).save(any());
//    }

//    @Test
//    void apply_ShouldThrowException_WhenSalaryIsOutOfRange() {
//        User user = User.builder().email("valid@example.com").baseSalary(new BigDecimal("20000000")).build();
//
//        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
//                .expectError(DomainValidationException.class)
//                .verify();
//
//        verify(logger).error(any());
//        verify(userRepository, never()).existsByEmail(any());
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void apply_ShouldHandleUnexpectedError() {
//        User user = User.builder().email("error@example.com").baseSalary(new BigDecimal("5000")).build();
//        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.error(new RuntimeException("Database error")));
//
//        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
//                .expectError(RuntimeException.class)
//                .verify();
//
//        verify(logger).error("Error creando usuario: {}", "Database error");
//        verify(userRepository, never()).save(user);
//    }
}