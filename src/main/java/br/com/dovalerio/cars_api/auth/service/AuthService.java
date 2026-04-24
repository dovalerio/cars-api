package br.com.dovalerio.cars_api.auth.service;

import br.com.dovalerio.cars_api.auth.dto.LoginRequest;
import br.com.dovalerio.cars_api.auth.dto.TokenResponse;
import br.com.dovalerio.cars_api.security.jwt.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Object principal = authentication.getPrincipal();
        UserDetails userDetails = principal instanceof UserDetails user
            ? user
            : userDetailsService.loadUserByUsername(authentication.getName());

        String token = jwtService.generateToken(userDetails);

        return new TokenResponse(token);
    }
}