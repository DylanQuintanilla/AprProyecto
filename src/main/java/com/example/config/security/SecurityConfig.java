package com.example.config.security;

// Importaciones de los manejadores de excepciones (NUEVO)
import com.example.config.exception.CustomAccessDeniedHandler;
import com.example.config.exception.CustomAuthenticationEntryPoint;

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

    // --- INYECTAMOS LOS MANEJADORES DE ERRORES (NUEVO) ---
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    // --- FIN DE INYECCIONES ---

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {

                    // EndPoints Publicos:
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                    http.requestMatchers("/api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();


                    // EndPoints Privados (Protegidos):

                    // --- Pacientes ---
                    http.requestMatchers(HttpMethod.GET, "/pacientes", "/pacientes/**")
                            .hasAnyAuthority("READ");
                    http.requestMatchers(HttpMethod.POST, "/pacientes")
                            .hasAnyAuthority("CREATE");
                    http.requestMatchers(HttpMethod.PUT, "/pacientes/**")
                            .hasAnyAuthority("UPDATE");
                    http.requestMatchers(HttpMethod.DELETE, "/pacientes/**")
                            .hasAuthority("DELETE");

                    // --- Dentistas ---
                    http.requestMatchers(HttpMethod.GET, "/dentistas", "/dentistas/**")
                            .hasAnyAuthority("READ");
                    http.requestMatchers(HttpMethod.POST, "/dentistas")
                            .hasAuthority("CREATE");
                    http.requestMatchers(HttpMethod.PUT, "/dentistas/**")
                            .hasAuthority("UPDATE");
                    http.requestMatchers(HttpMethod.DELETE, "/dentistas/**")
                            .hasAuthority("DELETE");

                    // --- Citas ---
                    http.requestMatchers(HttpMethod.GET, "/citas", "/citas/**")
                            .hasAuthority("READ");
                    http.requestMatchers(HttpMethod.POST, "/citas")
                            .hasAuthority("CREATE");
                    http.requestMatchers(HttpMethod.PUT, "/citas/**")
                            .hasAuthority("UPDATE");
                    http.requestMatchers(HttpMethod.DELETE, "/citas/**")
                            .hasAuthority("DELETE");

                    // --- Consultorios, Especialidades, Enfermedades, Tratamientos, etc. ---
                    http.requestMatchers("/consultorios/**", "/especialidades/**", "/enfermedades/**", "/tratamientos/**")
                            .hasAnyRole("ADMIN", "DOCTOR");

                    // --- Antecedentes Medicos ---
                    http.requestMatchers("/antecedentes-medicos/**")
                            .hasAnyRole("ADMIN", "DOCTOR");

                    // Denegar todo lo demás que no esté configurado
                    http.anyRequest().authenticated();
                })

                // --- AÑADIMOS EL MANEJO DE EXCEPCIONES DE SEGURIDAD (NUEVO) ---
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // --- FIN DEL BLOQUE DE EXCEPCIONES ---

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