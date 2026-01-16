package co.istad.iptidentityservice.security;

import co.istad.iptidentityservice.domain.User;
import co.istad.iptidentityservice.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User loggedInUser = userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"User not found"
                )
        );

//        String[] roles = loggedInUser.getRoles().stream()
//                .map(Role::getName)
//                .toArray(String[]::new);

        List<GrantedAuthority> authorities = new ArrayList<>();
        loggedInUser.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        UserDetails userSecurity = org.springframework.security.core.userdetails.User.builder()
                .username(loggedInUser.getUsername())
                .password(loggedInUser.getPassword())
//                .roles(roles)
                .authorities(authorities)
                .build();

        log.info("UserDetailsServiceImpl loadUserByUsername = {}", userSecurity.getUsername());
        log.info("UserDetailsServiceImpl loadUserRoles = {}" ,userSecurity.getAuthorities());
        return userSecurity;
    }
}
