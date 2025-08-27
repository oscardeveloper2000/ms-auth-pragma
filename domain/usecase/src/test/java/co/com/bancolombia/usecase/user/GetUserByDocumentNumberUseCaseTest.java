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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GetUserByDocumentNumberUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoggerPort logger;

    private GetUserByDocumentNumberUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetUserByDocumentNumberUseCase(userRepository, logger);
    }

    private User buildValidUser() {
        return User.builder()
                .id(1L)
                .documentNumber("123456789")
                .email("test@example.com")
                .build();
    }

    @Test
    void shouldReturnUser_WhenDocumentNumberExists() {
        // given
        User user = buildValidUser();
        when(userRepository.findByDocumentNumber("123456789")).thenReturn(Mono.just(user));

        // when - then
        StepVerifier.create(useCase.apply("123456789"))
                .expectNextMatches(u -> u.getDocumentNumber().equals("123456789"))
                .verifyComplete();

        verify(logger).info("Iniciando consulta de usuario por documento");
        verify(logger).debug(eq("Buscando usuario: documentNumber={}"), eq("12***89"));
        verify(logger).info(eq("Usuario encontrado: id={}, documentNumber={}"), eq(1L), eq("12***89"));
    }

    @Test
    void shouldThrowException_WhenDocumentNumberIsNull() {
        // when - then
        StepVerifier.create(useCase.apply(null))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("El número de documento es obligatorio"))
                .verify();

        verify(logger).warn("Número de documento inválido: <vacío>");
        verify(userRepository, never()).findByDocumentNumber(any());
    }

    @Test
    void shouldThrowException_WhenDocumentNumberIsBlank() {
        // when - then
        StepVerifier.create(useCase.apply("   "))
                .expectError(DomainValidationException.class)
                .verify();

        verify(logger).warn("Número de documento inválido: <vacío>");
        verify(userRepository, never()).findByDocumentNumber(any());
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findByDocumentNumber("123456789")).thenReturn(Mono.empty());

        // when - then
        StepVerifier.create(useCase.apply("123456789"))
                .expectErrorMatches(e -> e instanceof DomainValidationException &&
                        e.getMessage().contains("Usuario no encontrado"))
                .verify();

        verify(logger).warn(eq("Usuario no encontrado: documentNumber={}"), eq("12***89"));
    }

    @Test
    void shouldPropagateError_WhenRepositoryFails() {
        // given
        when(userRepository.findByDocumentNumber("123456789"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        // when - then
        StepVerifier.create(useCase.apply("123456789"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("DB error"))
                .verify();

        verify(logger).error(startsWith("Error consultando usuario por documento: {}"), any(RuntimeException.class));
    }
}
