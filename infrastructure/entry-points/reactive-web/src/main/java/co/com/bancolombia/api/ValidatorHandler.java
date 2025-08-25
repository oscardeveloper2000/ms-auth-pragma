package co.com.bancolombia.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidatorHandler {

     private final Validator validator;

    public <T> Mono<T> validate(T object) {
        return Mono.defer(() -> {
            Set<ConstraintViolation<T>> violations = validator.validate(object);
            if (!violations.isEmpty())
                return Mono.error(new ConstraintViolationException(violations));
            return Mono.just(object);
        });
    }
}
