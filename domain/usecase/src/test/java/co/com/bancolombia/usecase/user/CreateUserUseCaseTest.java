package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.role.Role;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.EncryptPasswordGateway;
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
    private RoleRepository roleRepository;
    @Mock
    private LoggerPort logger;
    @Mock
    private EncryptPasswordGateway encryptPasswordGateway;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createUserUseCase = new CreateUserUseCase(userRepository, roleRepository, logger, encryptPasswordGateway);
    }

    private User buildValidUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .roleId(2L)
                .passwordHash("plainpass")
                .build();
    }

//    @Test
//    void shouldCreateUserSuccessfully_WhenEmailNotRegisteredAndRoleExists() {
//        User user = buildValidUser();
//        Role role = Role.builder().id(2L).build();
//        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
//        when(roleRepository.findById(user.getRoleId())).thenReturn(Mono.just(role));
//        when(encryptPasswordGateway.encryptPassword(user.getPasswordHash())).thenReturn("encrypted");
//        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
//
//        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
//                .expectNextMatches(saved ->
//                        saved.getEmail().equals("test@example.com") &&
//                        saved.getBaseSalary().compareTo(new BigDecimal("5000000")) == 0 &&
//                        saved.getRoleId().equals(2L) &&
//                        saved.getPasswordHash().equals("encrypted")
//                )
//                .verifyComplete();
//
//        verify(logger).info("Iniciando creación de usuario");
//        verify(logger).info(contains("Usuario creado exitosamente"), any(), any());
//        verify(userRepository).save(any(User.class));
//    }

//    @Test
//    void shouldThrowException_WhenEmailAlreadyRegistered() {
//        User user = buildValidUser();
//        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));
//
//        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
//                .expectError(UserAlreadyExistsException.class)
//                .verify();
//
//        verify(logger).warn(contains("Intento de creación con email ya existente"), any());
//        verify(userRepository, never()).save(any());
//    }

//    @Test
//    void shouldThrowException_WhenEmailInvalid() {
//        User user = buildValidUser();
//        user.setEmail("invalidEmail");
//
//        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
//                .expectErrorMatches(e -> e instanceof DomainValidationException &&
//                        e.getMessage().contains("El correo_electronico no tiene un formato válido"))
//                .verify();
//
//        verify(userRepository, never()).existsByEmail(any());
//        verify(userRepository, never()).save(any());
//    }

    @Test
    void shouldThrowException_WhenSalaryIsNull() {
        User user = buildValidUser();
        user.setBaseSalary(null);

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("salario_base es obligatorio"))
                .verify();
    }

    @Test
    void shouldThrowException_WhenSalaryIsNegative() {
        User user = buildValidUser();
        user.setBaseSalary(new BigDecimal("-10"));

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("salario_base debe estar entre 0 y 15000000"))
                .verify();
    }

    @Test
    void shouldThrowException_WhenSalaryExceedsMax() {
        User user = buildValidUser();
        user.setBaseSalary(new BigDecimal("20000000"));

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("salario_base debe estar entre 0 y 15000000"))
                .verify();
    }

    @Test
    void shouldThrowException_WhenRoleNotFound() {
        User user = buildValidUser();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(roleRepository.findById(user.getRoleId())).thenReturn(Mono.empty());

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("Role not found"))
                .verify();
    }

    @Test
    void shouldEncryptPassword_WhenCreatingUser() {
        User user = buildValidUser();
        Role role = Role.builder().id(2L).build();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(roleRepository.findById(user.getRoleId())).thenReturn(Mono.just(role));
        when(encryptPasswordGateway.encryptPassword(user.getPasswordHash())).thenReturn("encrypted");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(createUserUseCase.apply(Mono.just(user)))
                .expectNextMatches(saved -> "encrypted".equals(saved.getPasswordHash()))
                .verifyComplete();

        verify(encryptPasswordGateway).encryptPassword("plainpass");
    }
}