package co.istad.Api_Gateway.route;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final TokenRelayGatewayFilterFactory tokenRelayGatewayFilterFactory;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Route
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                // Rewrite path before forwarding
                                .rewritePath("/users(?<segment>.*)", "/api/v1/users${segment}")
                                // Apply TokenRelay filter to forward OAuth2 token
                                .filter(tokenRelayGatewayFilterFactory.apply())
                        )
                        .uri("http://localhost:8081")
                )

                // Product Service Route
                .route("product-service", r -> r
                        .path("/products/**")
                        .filters(f -> f
                                // Rewrite path before forwarding
                                .rewritePath("/products(?<segment>.*)", "/api/v1/products${segment}")
                                // Apply TokenRelay filter to forward OAuth2 token
                                .filter(tokenRelayGatewayFilterFactory.apply())
                        )
                        .uri("http://localhost:8082")
                )

                .build();
    }
}