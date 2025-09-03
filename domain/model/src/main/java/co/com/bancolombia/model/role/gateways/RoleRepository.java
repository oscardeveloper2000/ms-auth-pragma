package co.com.bancolombia.model.role.gateways;

import co.com.bancolombia.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findById(Long id);
}
