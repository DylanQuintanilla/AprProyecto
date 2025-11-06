package com.example.config.security;

import com.example.config.security.filter.JwtTokenValidator;
import com.example.config.security.util.JwtUtils;
import com.example.service.implementation.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {

                    // EndPoints Publicos:
                    // Permite el acceso a los endpoints de autenticación y la documentación de Swagger
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                    http.requestMatchers("/api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();


                    // EndPoints Privados (Protegidos):

                    // --- Pacientes ---
                    // Roles USER, DOCTOR, ADMIN pueden leer.
                    http.requestMatchers(HttpMethod.GET, "/pacientes", "/pacientes/**")
                            .hasAnyAuthority("READ");
                    // Roles USER, DOCTOR, ADMIN pueden crear.
                    http.requestMatchers(HttpMethod.POST, "/pacientes")
                            .hasAnyAuthority("CREATE");
                    // Roles DOCTOR, ADMIN pueden actualizar.
                    http.requestMatchers(HttpMethod.PUT, "/pacientes/**")
                            .hasAnyAuthority("UPDATE");
                    // Solo ADMIN puede borrar.
                    http.requestMatchers(HttpMethod.DELETE, "/pacientes/**")
                            .hasAuthority("DELETE");

                    // --- Dentistas ---
                    // Roles DOCTOR, ADMIN pueden leer.
                    http.requestMatchers(HttpMethod.GET, "/dentistas", "/dentistas/**")
                            .hasAnyAuthority("READ");
                    // Solo ADMIN puede crear, actualizar o borrar dentistas.
                    http.requestMatchers(HttpMethod.POST, "/dentistas")
                            .hasAuthority("CREATE");
                    http.requestMatchers(HttpMethod.PUT, "/dentistas/**")
                            .hasAuthority("UPDATE");
                    http.requestMatchers(HttpMethod.DELETE, "/dentistas/**")
                            .hasAuthority("DELETE");

                    // --- Citas ---
                    // Todos los roles (USER, DOCTOR, ADMIN) pueden gestionar citas
                    http.requestMatchers(HttpMethod.GET, "/citas", "/citas/**")
                            .hasAuthority("READ");
                    http.requestMatchers(HttpMethod.POST, "/citas")
                            .hasAuthority("CREATE");
                    http.requestMatchers(HttpMethod.PUT, "/citas/**")
                            .hasAuthority("UPDATE");
                    // Solo ADMIN puede borrar citas (por seguridad)
                    http.requestMatchers(HttpMethod.DELETE, "/citas/**")
                            .hasAuthority("DELETE");

                    // --- Consultorios, Especialidades, Enfermedades, Tratamientos, etc. ---
                    // Solo DOCTOR y ADMIN pueden gestionar estas entidades.
                    http.requestMatchers("/consultorios/**", "/especialidades/**", "/enfermedades/**", "/tratamientos/**")
                            .hasAnyRole("ADMIN", "DOCTOR");

                    // --- Antecedentes Medicos ---
                    // Solo DOCTOR y ADMIN pueden gestionar antecedentes.
                    http.requestMatchers("/antecedentes-medicos/**")
                            .hasAnyRole("ADMIN", "DOCTOR");


                    // Denegar todo lo demás que no esté configurado
                    http.anyRequest().denyAll();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}