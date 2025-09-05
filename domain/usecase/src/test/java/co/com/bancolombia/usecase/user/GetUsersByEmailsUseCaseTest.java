package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.user.UserBasicInfo;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class GetUsersByEmailsUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoggerPort logger;

    private GetUsersByEmailsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetUsersByEmailsUseCase(userRepository, logger);
    }

    @Test
    void shouldReturnUsersByEmails() {
        List<String> emails = Arrays.asList("a@email.com", "b@email.com");
        UserBasicInfo user1 = UserBasicInfo.builder().email("a@email.com").build();
        UserBasicInfo user2 = UserBasicInfo.builder().email("b@email.com").build();

        when(userRepository.findByEmails(emails)).thenReturn(Flux.just(user1, user2));

        StepVerifier.create(useCase.apply(emails))
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(logger).info(contains("Buscando usuarios por emails"), eq(emails));
        verify(logger, times(2)).info(contains("Usuario encontrado"), any());
    }

    @Test
    void shouldReturnEmptyWhenNoUsersFound() {
        List<String> emails = Arrays.asList("c@email.com");
        when(userRepository.findByEmails(emails)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.apply(emails))
                .verifyComplete();

        verify(logger).info(contains("Buscando usuarios por emails"), eq(emails));
        verify(logger, never()).info(contains("Usuario encontrado"), any());
    }
}