package co.istad.Api_Gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerSecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(
            @RequestBody LoginRequest loginRequest,
            ServerWebExchange exchange
    ) {
        log.debug("Login attempt for user: {}", loginRequest.getUsername());

        // Create authentication token
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                );

        return authenticationManager.authenticate(authToken)
                .flatMap(authentication -> {
                    // Create security context
                    SecurityContext context = new SecurityContextImpl(authentication);

                    // Save security context in session
                    return securityContextRepository
                            .save(exchange, context)
                            .then(Mono.defer(() -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("success", true);
                                response.put("message", "Login successful");
                                response.put("username", authentication.getName());
                                response.put("redirectUrl", "/products");

                                log.info("User {} logged in successfully", authentication.getName());
                                return Mono.just(ResponseEntity.ok(response));
                            }));
                })
                .onErrorResume(BadCredentialsException.class, e -> {
                    log.warn("Failed login attempt for user: {}", loginRequest.getUsername());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid username or password");
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
                })
                .onErrorResume(e -> {
                    log.error("Login error: ", e);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "An error occurred during login");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }

    // DTO for login request
    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}