package br.gov.biblioteca_ag_backend.security;

import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;
import br.gov.biblioteca_ag_backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfigurations {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
//
//        return httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(author -> author
//
//                        .requestMatchers("/auth/login").permitAll()
//
//                        .requestMatchers(HttpMethod.GET, "/usuarios").hasAnyRole("ADMIN", "FUNCIONARIO", "USUARIO")
//                        .requestMatchers(HttpMethod.GET, "/usuarios/{id}").hasAnyRole("ADMIN", "FUNCIONARIO", "USUARIO")
//                        .requestMatchers(HttpMethod.POST, "/usuarios").hasAnyRole("ADMIN", "FUNCIONARIO")
//                        .requestMatchers(HttpMethod.PUT, "/usuarios/{id}").hasAnyRole("ADMIN", "FUNCIONARIO")
//                        .requestMatchers(HttpMethod.DELETE, "/usuarios/{id}").hasAnyRole("ADMIN", "FUNCIONARIO")
//
//                        .anyRequest().authenticated()
//                )
//
//                .build();
//    }
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
import java.io.IOException;

@Component
public class SecurityConfiguration extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfiguration(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            String email = tokenService.validateToken(token);
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null && usuario.isAtivo()) {
                UsuarioUserDetails userDetails = new UsuarioUserDetails(usuario);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua o fluxo da requisição para os próximos filtros e o Controller
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}