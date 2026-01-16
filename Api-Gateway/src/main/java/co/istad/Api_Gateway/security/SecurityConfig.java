package co.istad.Api_Gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.Arrays;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, ex2) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
                            exchange.getResponse().getHeaders()
                                    .setLocation(URI.create(frontendUrl + "/login"));
                            return exchange.getResponse().setComplete();
                        })
                )

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                "/",
                                "/login/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/logout",
                                "/logout-success",
                                "/error",
                                "/favicon.ico",
                                "/_next/**",
                                "/images/**",
                                "/fonts/**",
                                "/api/public/**",
                                "/api/auth/login"
                        ).permitAll()
                        .anyExchange().authenticated()
                )

                // Form Login Configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(
                                new RedirectServerAuthenticationSuccessHandler(frontendUrl + "/products")
                        )
                )

                // OAuth2 Login Configuration
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(
                                new RedirectServerAuthenticationSuccessHandler(frontendUrl + "/products")
                        )
                )

                .oauth2Client(org.springframework.security.config.Customizer.withDefaults())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(createLogoutSuccessHandler())
                )

                .build();
    }

    /**
     * In-memory user store for development
     * In production, replace with database-backed UserDetailsService
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("Thoung168")
                .password(passwordEncoder().encode("qwer"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("USER", "ADMIN")
                .build();

        return new MapReactiveUserDetailsService(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Reactive Authentication Manager for manual authentication
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            MapReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    /**
     * Security Context Repository for storing authentication in WebSession
     */
    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(frontendUrl, gatewayUrl));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private ServerLogoutSuccessHandler createLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("/login"));
        return handler;
    }
}