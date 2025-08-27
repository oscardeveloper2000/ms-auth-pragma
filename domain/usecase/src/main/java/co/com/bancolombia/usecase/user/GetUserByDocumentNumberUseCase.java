package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.common.LoggerPort;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.commom.DomainValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class GetUserByDocumentNumberUseCase implements GetUserByDocumentNumber {

    private final UserRepository userRepository;
    private final LoggerPort logger;


    @Override
    public Mono<User> apply(String documentNumber) {
        logger.info("Iniciando consulta de usuario por documento");

        if (documentNumber == null || documentNumber.isBlank()) {
            logger.warn("Número de documento inválido: <vacío>");
            return Mono.error(new DomainValidationException("El número de documento es obligatorio"));
        }

        String doc = documentNumber.trim();
        logger.debug("Buscando usuario: documentNumber={}", maskDocument(doc));

        return userRepository.findByDocumentNumber(doc)
                .doOnNext(u -> logger.info("Usuario encontrado: id={}, documentNumber={}", u.getId(), maskDocument(doc)))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Usuario no encontrado: documentNumber={}", maskDocument(doc));
                    return Mono.error(new DomainValidationException("Usuario no encontrado"));
                }))
                .doOnError(e -> logger.error("Error consultando usuario por documento: {}", e));
    }

    private String maskDocument(String doc) {
        if (doc == null || doc.isBlank()) return "N/A";
        String s = doc.trim();
        if (s.length() <= 4) return "***";
        return s.substring(0, 2) + "***" + s.substring(s.length() - 2);
    }
}