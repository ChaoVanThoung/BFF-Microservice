package co.istad.Api_Gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Value("${app.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF for development/BFF pattern simplicity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                "/",
                                "/login",
                                "/oauth2/**",
                                "/logout",
                                "/error",
                                "/favicon.ico",
                                "/_next/**",
                                "/images/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )

//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/products"))
//                )

                .oauth2Login(oauth2 -> oauth2
                        // Forces Spring to use your Next.js login page
                        .loginPage("/login")
                        .authenticationSuccessHandler(
                                new RedirectServerAuthenticationSuccessHandler("/")
                        )
                )




                // Keep OAuth2 client capabilities for Token Relay
                .oauth2Client(org.springframework.security.config.Customizer.withDefaults())

                .build();
    }
}