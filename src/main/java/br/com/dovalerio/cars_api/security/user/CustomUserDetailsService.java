package br.com.dovalerio.cars_api.security.user;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = Map.of(
            "admin", new AuthUser(
                    "admin",
                    "$2a$10$b7PLf0bavg6LvEPssJFtlOxW5iU/mXPudKwNY4MUFudGTXxdlMLE.",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            ),
            "user", new AuthUser(
                    "user",
                    "$2a$10$EjmyaXQuUD02iqAgYf1oBea70sQ771z52La/9oxUSSHmE1ujVr1YC",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            )
    );

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}