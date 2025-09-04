package co.com.bancolombia.r2dbc.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.UserBasicInfo;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User/* change for domain model */,
        UserEntity/* change for adapter model */,
        Long,
        UserReactiveRepository
> implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> findByDocumentNumber(String docNumber) {
        return repository.findByDocumentNumber(docNumber);

    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Flux<UserBasicInfo> findByEmails(List<String> emails) {
        return repository.findAllByEmailIn(emails)
                .map(entity -> UserBasicInfo.builder()
                        .email(entity.getEmail())
                        .firstName(entity.getFirstName())
                        .lastName(entity.getLastName())
                        .baseSalary(entity.getBaseSalary())
                        .build());
    }


}
