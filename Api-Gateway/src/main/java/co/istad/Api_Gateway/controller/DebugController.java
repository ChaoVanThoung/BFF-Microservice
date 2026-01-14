package co.istad.Api_Gateway.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    /**
     * Test endpoint to verify gateway is running
     */
    @GetMapping("/public/health")
    public Mono<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "API Gateway");
        response.put("timestamp", Instant.now().toString());
        return Mono.just(response);
    }

    /**
     * Get OAuth2 token information - useful for debugging
     */
    @GetMapping("/token-info")
    public Mono<Map<String, Object>> getTokenInfo(
            @RegisteredOAuth2AuthorizedClient("gateway-client") OAuth2AuthorizedClient authorizedClient) {

        Map<String, Object> info = new HashMap<>();

        // Client information
        info.put("clientName", authorizedClient.getClientRegistration().getClientName());
        info.put("clientId", authorizedClient.getClientRegistration().getClientId());
        info.put("registrationId", authorizedClient.getClientRegistration().getRegistrationId());

        // Access token
        info.put("accessToken", authorizedClient.getAccessToken().getTokenValue());
        info.put("accessTokenIssuedAt", authorizedClient.getAccessToken().getIssuedAt());
        info.put("accessTokenExpiresAt", authorizedClient.getAccessToken().getExpiresAt());
        info.put("accessTokenScopes", authorizedClient.getAccessToken().getScopes());

        // Refresh token
        if (authorizedClient.getRefreshToken() != null) {
            info.put("refreshToken", authorizedClient.getRefreshToken().getTokenValue());
            info.put("refreshTokenIssuedAt", authorizedClient.getRefreshToken().getIssuedAt());
        } else {
            info.put("refreshToken", "No refresh token available");
        }

        // Principal name
        info.put("principalName", authorizedClient.getPrincipalName());

        return Mono.just(info);
    }

    /**
     * Get user information from OIDC
     */
    @GetMapping("/user-info")
    public Mono<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {

        Map<String, Object> info = new HashMap<>();

        if (oidcUser != null) {
            info.put("username", oidcUser.getName());
            info.put("email", oidcUser.getEmail());
            info.put("fullName", oidcUser.getFullName());
            info.put("subject", oidcUser.getSubject());
            info.put("claims", oidcUser.getClaims());
            info.put("attributes", oidcUser.getAttributes());

            // ID Token
            if (oidcUser.getIdToken() != null) {
                info.put("idToken", oidcUser.getIdToken().getTokenValue());
                info.put("idTokenClaims", oidcUser.getIdToken().getClaims());
            }
        } else {
            info.put("error", "No OIDC user found");
        }

        return Mono.just(info);
    }

    /**
     * Simple authenticated endpoint
     */
    @GetMapping("/authenticated")
    public Mono<Map<String, Object>> authenticated(@AuthenticationPrincipal OAuth2User oauth2User) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are authenticated!");
        response.put("username", oauth2User.getName());
        response.put("attributes", oauth2User.getAttributes());

        return Mono.just(response);
    }

    /**
     * Endpoint to test if you're logged in
     */
    @GetMapping("/")
    public Mono<String> home(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            return Mono.just("Welcome " + oauth2User.getAttribute("name") + "! You are logged in.");
        }
        return Mono.just("You are not logged in. This should trigger OAuth2 login.");
    }
}