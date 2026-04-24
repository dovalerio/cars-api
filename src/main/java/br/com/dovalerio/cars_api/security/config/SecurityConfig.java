package br.com.dovalerio.cars_api.security.config;

import br.com.dovalerio.cars_api.common.response.ErrorResponse;
import br.com.dovalerio.cars_api.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            UserDetailsService userDetailsService
    ) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authException) ->
                        writeErrorResponse(
                            response,
                            HttpStatus.UNAUTHORIZED,
                            "Authentication required or invalid token",
                            request.getRequestURI()
                        )
                    )
                    .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeErrorResponse(
                            response,
                            HttpStatus.FORBIDDEN,
                            "Access denied",
                            request.getRequestURI()
                        )
                    )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                        "/api-docs",
                        "/api-docs/**",
                        "/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers(GET, "/veiculos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(POST, "/veiculos/**").hasRole("ADMIN")
                        .requestMatchers(PUT, "/veiculos/**").hasRole("ADMIN")
                        .requestMatchers(PATCH, "/veiculos/**").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/veiculos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            String path
    ) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        String escapedMessage = escapeJson(errorResponse.getMessage());
        String escapedPath = escapeJson(errorResponse.getPath());
        String escapedError = escapeJson(errorResponse.getError());
        String escapedTimestamp = escapeJson(errorResponse.getTimestamp().toString());
        String jsonBody = "{"
            + "\"status\":" + errorResponse.getStatus() + ","
            + "\"error\":\"" + escapedError + "\","
            + "\"message\":\"" + escapedMessage + "\","
            + "\"path\":\"" + escapedPath + "\","
            + "\"timestamp\":\"" + escapedTimestamp + "\""
            + "}";

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonBody);
        response.getWriter().flush();
        }

        private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}