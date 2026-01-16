package co.istad.iptidentityservice.security;

import co.istad.iptidentityservice.domain.Role;
import co.istad.iptidentityservice.domain.User;
import co.istad.iptidentityservice.features.oauth2.JpaRegisteredClientRepository;
import co.istad.iptidentityservice.features.role.RoleRepository;
import co.istad.iptidentityservice.features.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityInit {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JpaRegisteredClientRepository jpaRegisteredClientRepository;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUuid(UUID.randomUUID().toString());
            user.setUsername("Thoung168");
            user.setPassword(passwordEncoder.encode("qwer"));
            user.setEmail("chaovanthoung168.com");
            user.setDob(LocalDate.of(2004, 2, 23));
            user.setGender("Male");
            user.setProfileImage("default_profile.jpg");
            user.setCoverImage("default_cover.jpg");
            user.setFamilyName("Chan");
            user.setGivenName("Chhaya");
            user.setPhoneNumber("0962226229");
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setIsEnabled(true);

            // Assign role to user
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("SUPER_ADMIN"));
            roles.add(roleRepository.findByName("USER"));
            user.setRoles(roles);

            userRepository.save(user);
            log.info("User has been saved: {}", user.getId());
        }
    }

    @PostConstruct
    void initOAuth2() {

        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofDays(3))
                .reuseRefreshTokens(false) // refresh token rotation
                .refreshTokenTimeToLive(Duration.ofDays(5))
                .build();

        ClientSettings clientSettings = ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(false)
                .build();

        var itpStandard = RegisteredClient.withId("itp-standard")
                .clientId("itp-standard")
                .clientSecret(passwordEncoder.encode("qwerqwer")) // store in secret manager
                .scopes(scopes -> {
                    scopes.add(OidcScopes.OPENID); // required!
                    scopes.add(OidcScopes.PROFILE);
                    scopes.add(OidcScopes.EMAIL);
                })
                .redirectUris(uris -> {
                    uris.add("http://localhost:9090/login/oauth2/code/itp-standard");
                    uris.add("http://localhost:9090");
                    uris.add("https://cstad.edu.kh/");
                })
                .postLogoutRedirectUris(uris -> {
                    uris.add("http://localhost:9090");
                })
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) //TODO: grant_type:client_credentials, client_id & client_secret, redirect_uri
                .authorizationGrantTypes(grantTypes -> {
                    grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                    grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                })
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .build();

        RegisteredClient registeredClient = jpaRegisteredClientRepository.findByClientId("itp-standard");
        log.info("Registered client: {}", registeredClient);

        if (registeredClient == null) {
            jpaRegisteredClientRepository.save(itpStandard);
        }

        // Add Gateway Client Registration
        var gatewayClient = RegisteredClient.withId("api-gateway")
                .clientId("api-gateway")
                .clientSecret(passwordEncoder.encode("gateway-secret"))
                .scopes(scopes -> {
                    scopes.add(OidcScopes.OPENID);
                    scopes.add(OidcScopes.PROFILE);
                    scopes.add(OidcScopes.EMAIL);
                    scopes.add("read");
                    scopes.add("write");
                })
                .redirectUris(uris -> {
                    // Gateway OAuth2 callback
                    uris.add("http://localhost:8080/login/oauth2/code/gateway-client");
                    // Post-login redirect
                    uris.add("http://localhost:8080");
                })
                .postLogoutRedirectUris(uris -> {
                    uris.add("http://localhost:8080/logout-success");
                })


                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(grantTypes -> {
                    grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(false)
                        .build())
                .tokenSettings(tokenSettings)
                .build();

        if (jpaRegisteredClientRepository.findByClientId("api-gateway") == null) {
            jpaRegisteredClientRepository.save(gatewayClient);
        }

    }


}


