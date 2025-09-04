package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.UserBasicInfo;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GetUsersByEmail {
    Flux<UserBasicInfo> apply(List<String> emails);
}
