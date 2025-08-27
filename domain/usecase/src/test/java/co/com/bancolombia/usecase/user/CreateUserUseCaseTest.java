package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.commom.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
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

    private User buildValidUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully_WhenEmailNotRegistered() {
        // given
        User user = User.builder().email("test@example.com").baseSalary(new BigDecimal("5000")).build();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        // when - then
        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectNextMatches(saved ->
                        saved.getEmail().equals("test@example.com") &&
                                saved.getBaseSalary().compareTo(new BigDecimal("5000")) == 0
                )
                .verifyComplete();

        verify(logger).info("Iniciando creación de usuario");
        verify(logger).info(eq("Usuario creado exitosamente: id={}, email={}"), isNull(), eq("t***t@example.com"));
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyRegistered() {
        // given
        User user = User.builder().email("existing@example.com").baseSalary(new BigDecimal("5000")).build();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));

        // when - then
        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(logger).warn(eq("Intento de creación con email ya existente: {}"), eq("e***g@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenEmailInvalid() {
        // given
        User user = buildValidUser();
        user.setEmail("invalidEmail");

        // when - then
        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("El correo_electronico no tiene un formato válido"))
                .verify();

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenSalaryIsNull() {
        // given
        User user = buildValidUser();
        user.setBaseSalary(null);

        // when - then
        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("salario_base es obligatorio"))
                .verify();
    }

    @Test
    void shouldThrowException_WhenSalaryIsNegative() {
        // given
        User user = buildValidUser();
        user.setBaseSalary(new BigDecimal("-10"));

        // when - then
        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("salario_base debe estar entre 0 y 15000000"))
                .verify();
    }

    @Test
    void shouldThrowException_WhenSalaryExceedsMax() {
        // given
        User user = buildValidUser();
        user.setBaseSalary(new BigDecimal("20000000"));

        // when - then
        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("salario_base debe estar entre 0 y 15000000"))
                .verify();
    }
}
