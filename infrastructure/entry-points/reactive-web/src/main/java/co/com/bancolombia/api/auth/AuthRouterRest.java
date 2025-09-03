package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.user.UserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {
    private static final String PATH = "/api/v1/login";

    private final AuthHandler authHandler;

    @Bean
    public RouterFunction<ServerResponse> authRouterFunction(UserHandler handler) {
        return route(POST(PATH), authHandler::listenLogin);
    }
}
