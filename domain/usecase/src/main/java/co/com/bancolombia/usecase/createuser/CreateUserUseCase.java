package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class CreateUserUseCase implements CreateUser {

    private final UserRepository userRepository;

    // Validación de email (patrón simple y efectivo)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    @Override
    public Mono<User> apply(Mono<User> user) {
        return user.flatMap(userDTO -> {
            // Validaciones de dominio
            validateUserData(userDTO);

            return userRepository.existsByEmail(userDTO.getEmail())
                    .flatMap(exists -> {
                        if (exists) {
                            return Mono.error(new UserAlreadyExistsException("Ya existe el usuario con correo " + userDTO.getEmail()));
                        }
                        return userRepository.save(userDTO);
                    });
        });
    }

    private void validateUserData(User user) {
        // Email obligatorio y con formato válido
        String email = user.getEmail();
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new DomainValidationException("El correo_electronico no tiene un formato válido");
        }

        // Salario base numérico y en rango [0, 15000000]
        BigDecimal salary = user.getBaseSalary();
        if (salary == null) {
            throw new DomainValidationException("El salario_base es obligatorio");
        }
        if (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            throw new DomainValidationException("El salario_base debe estar entre 0 y 15000000");
        }
    }
}