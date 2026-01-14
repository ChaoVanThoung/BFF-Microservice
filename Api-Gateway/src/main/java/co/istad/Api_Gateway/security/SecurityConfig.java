package co.istad.Api_Gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                        // Public endpoints
                        .pathMatchers("/login/**", "/public/**", "/actuator/**").permitAll()
                        // All other requests require authentication
                        .anyExchange().authenticated()
                )
                // Enable OAuth2 Login (Authorization Code Flow with PKCE)
                .oauth2Login(Customizer.withDefaults())

                // Enable OAuth2 Client for token relay
                .oauth2Client(Customizer.withDefaults())

                // Logout configuration
                .logout(logout -> {
                    ServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
                    ((RedirectServerLogoutSuccessHandler) handler).setLogoutSuccessUrl(URI.create("http://localhost:9000/logout"));
                    logout.logoutSuccessHandler(handler);
                })

                // Disable CSRF for API Gateway (if needed)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}