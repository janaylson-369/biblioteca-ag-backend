package br.gov.biblioteca_ag_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(author -> author

                        .requestMatchers("/auth/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/usuarios").hasAnyRole("ADMIN", "FUNCIONARIO", "USUARIO")
                        .requestMatchers(HttpMethod.GET, "/usuarios/{id}").hasAnyRole("ADMIN", "FUNCIONARIO", "USUARIO")
                        .requestMatchers(HttpMethod.POST, "/usuarios").hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/{id}").hasAnyRole("ADMIN", "FUNCIONARIO")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/{id}").hasAnyRole("ADMIN", "FUNCIONARIO")

                        .anyRequest().authenticated()
                )

                .build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
