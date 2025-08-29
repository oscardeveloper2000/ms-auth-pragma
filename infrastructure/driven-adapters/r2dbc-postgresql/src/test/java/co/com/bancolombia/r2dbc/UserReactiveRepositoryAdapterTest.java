// package co.com.bancolombia.r2dbc;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.reactivecommons.utils.ObjectMapper;

// import org.springframework.data.domain.Example;

// import co.com.bancolombia.model.user.User;
// import co.com.bancolombia.r2dbc.entity.UserEntity;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.mockito.Mockito.mock;

// @ExtendWith(MockitoExtension.class)
// class UserReactiveRepositoryAdapterTest {
//     // TODO: change four you own tests

//     @InjectMocks
//     UserReactiveRepositoryAdapter repositoryAdapter;

//     @Mock
//     UserReactiveRepository repository;

//     @Mock
//     ObjectMapper mapper;

//     @Test
//     void mustFindUserById() {
//         UserEntity userEntity = UserEntity.builder()
//                 .id(1L)
//                 .email("john.doe@example.com")
//                 .build();
//         User user = User.builder()
//                 .id(1L)
//                 .email("john.doe@example.com")
//                 .build();

//         when(repository.findById(1L)).thenReturn(Mono.just(userEntity));
//         when(mapper.map(userEntity, User.class)).thenReturn(user);

//         Mono<User> result = repositoryAdapter.findById(1L);

//         StepVerifier.create(result)
//                 .expectNextMatches(u -> u.getId().equals(1L) && "john.doe@example.com".equals(u.getEmail()))
//                 .verifyComplete();
//     }


//     @Test
//     void mustFindAllUsers() {
//         UserEntity userEntity = UserEntity.builder()
//                 .id(2L)
//                 .email("jane.doe@example.com")
//                 .build();
//         User user = User.builder()
//                 .id(2L)
//                 .email("jane.doe@example.com")
//                 .build();

//         when(repository.findAll()).thenReturn(Flux.just(userEntity));
//         when(mapper.map(userEntity, User.class)).thenReturn(user);

//         Flux<User> result = repositoryAdapter.findAll();

//         StepVerifier.create(result)
//                 .expectNextMatches(u -> u.getId().equals(2L) && "jane.doe@example.com".equals(u.getEmail()))
//                 .verifyComplete();
//     }


//     @Test
//     void mustFindUsersByExample() {
//         User exampleUser = User.builder()
//                 .email("filter@example.com")
//                 .build();
//         UserEntity exampleEntity = UserEntity.builder()
//                 .email("filter@example.com")
//                 .build();

//         UserEntity foundEntity = UserEntity.builder()
//                 .id(3L)
//                 .email("filter@example.com")
//                 .build();
//         User mappedUser = User.builder()
//                 .id(3L)
//                 .email("filter@example.com")
//                 .build();

//         when(mapper.map(exampleUser, UserEntity.class)).thenReturn(exampleEntity);
//         when(repository.findAll(any(Example.class))).thenReturn(Flux.just(foundEntity));
//         when(mapper.map(foundEntity, User.class)).thenReturn(mappedUser);

//         Flux<User> result = repositoryAdapter.findByExample(exampleUser);

//         StepVerifier.create(result)
//                 .expectNextMatches(u -> u.getId().equals(3L) && "filter@example.com".equals(u.getEmail()))
//                 .verifyComplete();
//     }


//     @Test
//     void mustSaveUser() {
//         User userToSave = User.builder()
//                 .email("new.user@example.com")
//                 .build();
//         UserEntity dataToSave = UserEntity.builder()
//                 .email("new.user@example.com")
//                 .build();
//         UserEntity savedEntity = UserEntity.builder()
//                 .id(4L)
//                 .email("new.user@example.com")
//                 .build();
//         User savedUser = User.builder()
//                 .id(4L)
//                 .email("new.user@example.com")
//                 .build();

//         when(mapper.map(userToSave, UserEntity.class)).thenReturn(dataToSave);
//         when(repository.save(dataToSave)).thenReturn(Mono.just(savedEntity));
//         when(mapper.map(savedEntity, User.class)).thenReturn(savedUser);

//         Mono<User> result = repositoryAdapter.save(userToSave);

//         StepVerifier.create(result)
//                 .expectNextMatches(u -> u.getId().equals(4L) && "new.user@example.com".equals(u.getEmail()))
//                 .verifyComplete();
//     }

// }
