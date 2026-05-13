package edu.hyf.invoice.config;

import edu.hyf.invoice.auth.LoggingFilter;
import edu.hyf.invoice.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final LoggingFilter loggingFilter;

    // Encoder
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // To decide which route to protect, which role has the access
    // 1.csrf
    // 2.session management
    // 3.authorizeHttpRequest

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy((SessionCreationPolicy.STATELESS))) // disable session
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/v1/**").permitAll()
               //         .requestMatchers("/api/v1/auth/**").permitAll()
              //          .requestMatchers(HttpMethod.GET, "/api/v1/invoice/**").permitAll()
              //          .requestMatchers("/swagger-ui/**").hasRole("ADMIN")
              //          .requestMatchers("/api/vi/invoice/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loggingFilter, JwtAuthFilter.class);
        return http.build();
    }


}
