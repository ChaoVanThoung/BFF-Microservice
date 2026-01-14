package co.istad.Api_Gateway.route;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final TokenRelayGatewayFilterFactory tokenRelayGatewayFilterFactory;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                .rewritePath("/users(?<segment>.*)", "/api/v1/users${segment}")
                                .filter(tokenRelayGatewayFilterFactory.apply())
                        )
                        .uri("http://localhost:8081")
                )

//                .route("product-service", r -> r
//                        .path("/products/**")
//                        .filters(f -> f
//                                .rewritePath("/products(?<segment>.*)", "/api/v1/products${segment}")
//                                .filter(tokenRelayGatewayFilterFactory.apply())
//                        )
//                        .uri("http://localhost:8082")
//                )

                .route("product-service", r -> r
                        .path("/api/products/**") // use /api prefix
                        .filters(f -> f
                                .rewritePath("/api/products(?<segment>.*)", "/api/v1/products${segment}")
                                .filter(tokenRelayGatewayFilterFactory.apply())
                        )
                        .uri("http://localhost:8082")
                )

                .route("nextjs-bff", r -> r
                        .path("/bff/**")
                        .filters(f -> f
                                .tokenRelay()
                                .rewritePath("/bff(?<segment>/?.*)", "${segment}"))
                        .uri(frontendUrl))

                .route("nextjs-static", r -> r
                        .path("/_next/**", "/favicon.ico", "/images/**", "/fonts/**")
                        .uri(frontendUrl))

                .route("nextjs-products-pages", r -> r
                        .order(-1)
                        .path("/products/**") // Match the /products path
                        .filters(f -> f.tokenRelay()) // Pass the OAuth2 token to the backend if needed
                        .uri(frontendUrl) // Forward to http://localhost:3000
                )

                .route("nextjs-pages", r -> r
                        .order(1000)
                        .path("/**")
                        .and()
                        .not(p -> p.path("/logout", "/logout-success", "/oauth2/**", "/error"))
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri(frontendUrl))



                .build();
    }
}