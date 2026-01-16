package co.istad.Api_Gateway.route;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
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

                // Authentication endpoints - HIGHEST PRIORITY (handled by Gateway controllers)
                .route("auth-endpoints", r -> r
                        .order(-100) // Very high priority
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(0)) // Don't modify the path
                        .uri("no://op") // No-op URI, handled by @RestController
                )

                // User Service API Routes
                .route("user-service", r -> r
                        .order(0)
                        .path("/api/users/**")
                        .filters(f -> f
                                        .rewritePath("/api/users(?<segment>.*)", "/api/v1/users${segment}")
                                // Removed token relay - using session-based auth instead
                        )
                        .uri("http://localhost:8081")
                )

                // Product Service API Routes
                .route("product-service", r -> r
                        .order(0)
                        .path("/api/products/**")
                        .filters(f -> f
                                        .rewritePath("/api/products(?<segment>.*)", "/api/v1/products${segment}")
                                // Removed token relay - using session-based auth instead
                        )
                        .uri("http://localhost:8082")
                )

                // Next.js Static Assets (high priority, no auth required)
                .route("nextjs-static", r -> r
                        .order(100)
                        .path("/_next/**", "/favicon.ico", "/images/**", "/fonts/**")
                        .uri(frontendUrl)
                )

                // Next.js Pages (lowest priority, catch-all)
                .route("nextjs-pages", r -> r
                        .order(1000)
                        .path("/**")
                        .filters(f -> f.tokenRelay())
                        .uri(frontendUrl)
                )

                .build();
    }
}