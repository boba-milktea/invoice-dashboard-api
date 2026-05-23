package edu.hyf.invoice.config;

import edu.hyf.invoice.auth.LoggingFilter;
import edu.hyf.invoice.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    LoggingFilter loggingFilter () {
        return new LoggingFilter();
    }
    
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy((SessionCreationPolicy.STATELESS))) // disable session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/me").authenticated()

                        .requestMatchers(HttpMethod.GET,"/api/v1/users/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/users/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/users/**").hasRole("SUPER_ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/role").hasRole("SUPER_ADMIN")

                        .requestMatchers(
                                "/api/v1/clients/**",
                                "/api/v1/invoices/**",
                                "/api/v1/dashboard/**"
                        ).hasAnyRole("ADMIN", "SUPER_ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoggingFilter(), JwtAuthFilter.class);
        
        return http.build();
    }


}
