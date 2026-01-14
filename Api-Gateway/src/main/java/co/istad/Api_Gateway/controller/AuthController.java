package co.istad.Api_Gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.gateway.url}")
    private String gatewayUrl;

    /**
     * Initiates OAuth2 login flow
     * Returns the OAuth2 authorization URL for the frontend to redirect to
     */
    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RegisteredOAuth2AuthorizedClient("api-gateway-client") OAuth2AuthorizedClient authorizedClient) {

        Map<String, Object> response = new HashMap<>();

        if (oidcUser == null) {
            response.put("authenticated", false);
            response.put("user", null);
            return Mono.just(response);
        }

        // Build user info from OIDC claims (don't expose raw token)
        Map<String, Object> user = new HashMap<>();
        user.put("sub", oidcUser.getSubject());
        user.put("email", oidcUser.getEmail());
        user.put("name", oidcUser.getFullName());
        user.put("given_name", oidcUser.getGivenName());
        user.put("family_name", oidcUser.getFamilyName());

        // Get custom claims if present
        if (oidcUser.getClaim("uuid") != null) {
            user.put("uuid", oidcUser.getClaim("uuid"));
        }
        if (oidcUser.getClaim("roles") != null) {
            user.put("roles", oidcUser.getClaim("roles"));
        }
        if (oidcUser.getClaim("permissions") != null) {
            user.put("permissions", oidcUser.getClaim("permissions"));
        }

        response.put("authenticated", true);
        response.put("user", user);

        // Include token expiry info (not the token itself)
        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            response.put("expiresAt", authorizedClient.getAccessToken().getExpiresAt());
        }

        return Mono.just(response);
    }

    /**
     * Simple endpoint to check if user is authenticated
     * Returns 200 if authenticated, 401 if not (handled by security config)
     */
    @GetMapping("/status")
    public Mono<Map<String, Object>> getAuthStatus(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", oidcUser != null);
        return Mono.just(response);
    }
}