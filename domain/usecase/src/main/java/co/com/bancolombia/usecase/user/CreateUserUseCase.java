package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.commom.DomainValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
// ... existing code ...
import co.com.bancolombia.model.common.LoggerPort;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class CreateUserUseCase implements CreateUser {

    private final UserRepository userRepository;
    private final LoggerPort logger;


    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    @Override
    public Mono<User> apply(Mono<User> user) {
        return user
                .doOnSubscribe(sub -> logger.info("Iniciando creación de usuario"))
                .doOnNext(u -> logger.debug("Payload recibido: email={}, baseSalary={}",
                        maskEmail(u.getEmail()), u.getBaseSalary()))
                .flatMap(userDTO -> {
                    // Validaciones de dominio
                    logger.debug("Validando datos de usuario: email={}", maskEmail(userDTO.getEmail()));
                    validateUserData(userDTO);

                    return userRepository.existsByEmail(userDTO.getEmail())
                            .doOnNext(exists -> {
                                if (exists) {
                                    logger.warn("Intento de creación con email ya existente: {}", maskEmail(userDTO.getEmail()));
                                } else {
                                    logger.debug("Email no registrado previamente: {}", maskEmail(userDTO.getEmail()));
                                }
                            })
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new UserAlreadyExistsException("Ya existe el usuario con correo " + userDTO.getEmail()));
                                }
                                logger.info("Guardando nuevo usuario: email={}", maskEmail(userDTO.getEmail()));
                                return userRepository.save(userDTO);
                            });
                })
                .doOnSuccess(u -> {
                    if (u != null) {
                        logger.info("Usuario creado exitosamente: id={}, email={}", u.getId(), maskEmail(u.getEmail()));
                    } else {
                        logger.debug("Creación de usuario completada sin resultado (Mono vacío)");
                    }
                })
                .doOnError(e -> logger.error("Error creando usuario: {}", e));
    }

    private void validateUserData(User user) {

        String email = user.getEmail();
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new DomainValidationException("El correo_electronico no tiene un formato válido");
        }


        BigDecimal salary = user.getBaseSalary();
        if (salary == null) {
            throw new DomainValidationException("El salario_base es obligatorio");
        }
        if (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            throw new DomainValidationException("El salario_base debe estar entre 0 y 15000000");
        }
    }


    private String maskEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) return "N/A";
        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];
        if (local.length() <= 2) {
            return local.charAt(0) + "***@" + domain;
        }
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }
}