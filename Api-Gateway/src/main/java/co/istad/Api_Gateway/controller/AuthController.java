package co.istad.Api_Gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Get current user information
     * Works with both form-based authentication and OAuth2
     */
    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();

                    if (authentication == null || !authentication.isAuthenticated()) {
                        throw new RuntimeException("Not authenticated");
                    }

                    Map<String, Object> userInfo = new HashMap<>();
                    Object principal = authentication.getPrincipal();

                    // Handle OAuth2 user (OidcUser)
                    if (principal instanceof OidcUser oidcUser) {
                        userInfo.put("username", oidcUser.getPreferredUsername());
                        userInfo.put("email", oidcUser.getEmail());
                        userInfo.put("name", oidcUser.getFullName());
                        userInfo.put("sub", oidcUser.getSubject());
                        userInfo.put("givenName", oidcUser.getGivenName());
                        userInfo.put("familyName", oidcUser.getFamilyName());
                        userInfo.put("authType", "oauth2");
                    }
                    // Handle form-based authentication (UserDetails)
                    else if (principal instanceof UserDetails userDetails) {
                        userInfo.put("username", userDetails.getUsername());
                        userInfo.put("name", userDetails.getUsername());
                        userInfo.put("email", userDetails.getUsername() + "@example.com");
                        userInfo.put("authType", "form");
                    }
                    // Handle string principal (username)
                    else if (principal instanceof String username) {
                        userInfo.put("username", username);
                        userInfo.put("name", username);
                        userInfo.put("authType", "form");
                    }

                    userInfo.put("authenticated", true);
                    log.debug("Returning user info: {}", userInfo);
                    return userInfo;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Not authenticated")));
    }

    /**
     * Check if user is authenticated
     */
    @GetMapping("/status")
    public Mono<Map<String, Object>> getAuthStatus() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    Authentication auth = context.getAuthentication();
                    Map<String, Object> status = new HashMap<>();
                    boolean isAuthenticated = auth != null && auth.isAuthenticated()
                            && !"anonymousUser".equals(auth.getPrincipal());

                    status.put("authenticated", isAuthenticated);
                    if (isAuthenticated) {
                        status.put("username", auth.getName());
                    }
                    return status;
                })
                .defaultIfEmpty(Map.of("authenticated", false));
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public Mono<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("redirectUrl", "/login");
        return Mono.just(response);
    }
}