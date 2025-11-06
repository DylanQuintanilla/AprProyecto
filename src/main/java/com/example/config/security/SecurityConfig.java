package com.example.config.security;

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

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {

                    // --- EndPoints Publicos ---
                    auth.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();

                    // --- CORRECCIÓN EXPLÍCITA DE SWAGGER ---
                    auth.requestMatchers(
                            "/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",  // <-- Añadida la ruta exacta
                            "/v3/api-docs/**"
                    ).permitAll();


                    // --- Citas ---
                    auth.requestMatchers(HttpMethod.GET, "/citas", "/citas/**")
                            .hasAnyAuthority("citas:leer:propias", "citas:leer:asignadas", "citas:admin");
                    auth.requestMatchers(HttpMethod.POST, "/citas")
                            .hasAnyAuthority("citas:solicitar:propias", "citas:gestionar:asignadas", "citas:admin");
                    auth.requestMatchers(HttpMethod.PUT, "/citas/**")
                            .hasAnyAuthority("citas:gestionar:asignadas", "citas:admin");
                    auth.requestMatchers(HttpMethod.DELETE, "/citas/**")
                            .hasAuthority("citas:admin");

                    // --- Pacientes ---
                    auth.requestMatchers(HttpMethod.GET, "/pacientes")
                            .hasAnyAuthority("pacientes:leer:lista", "pacientes:admin");
                    auth.requestMatchers(HttpMethod.GET, "/pacientes/**")
                            .hasAnyAuthority("perfil:leer:propio", "pacientes:leer:perfil", "pacientes:admin");
                    auth.requestMatchers(HttpMethod.PUT, "/pacientes/**")
                            .hasAnyAuthority("perfil:actualizar:propio", "pacientes:admin");
                    auth.requestMatchers(HttpMethod.POST, "/pacientes")
                            .hasAuthority("pacientes:admin");
                    auth.requestMatchers(HttpMethod.DELETE, "/pacientes/**")
                            .hasAuthority("pacientes:admin");

                    // --- Dentistas ---
                    auth.requestMatchers(HttpMethod.GET, "/dentistas")
                            .hasAuthority("dentistas:admin");
                    auth.requestMatchers(HttpMethod.GET, "/dentistas/**")
                            .hasAnyAuthority("perfil:leer:propio", "dentistas:admin");
                    auth.requestMatchers(HttpMethod.PUT, "/dentistas/**")
                            .hasAnyAuthority("perfil:actualizar:propio", "dentistas:admin");
                    auth.requestMatchers(HttpMethod.POST, "/dentistas")
                            .hasAuthority("dentistas:admin");
                    auth.requestMatchers(HttpMethod.DELETE, "/dentistas/**")
                            .hasAuthority("dentistas:admin");

                    // --- Antecedentes Medicos ---
                    auth.requestMatchers(HttpMethod.GET, "/antecedentes-medicos", "/antecedentes-medicos/**")
                            .hasAnyAuthority("antecedentes:leer:propios", "antecedentes:leer:paciente", "antecedentes:admin");
                    auth.requestMatchers(HttpMethod.POST, "/antecedentes-medicos")
                            .hasAnyAuthority("antecedentes:gestionar:paciente", "antecedentes:admin");
                    auth.requestMatchers(HttpMethod.PUT, "/antecedentes-medicos/**")
                            .hasAnyAuthority("antecedentes:gestionar:paciente", "antecedentes:admin");
                    auth.requestMatchers(HttpMethod.DELETE, "/antecedentes-medicos/**")
                            .hasAuthority("antecedentes:admin");

                    // --- Clinica (Consultorios, Enfermedades, etc.) ---
                    auth.requestMatchers("/consultorios/**", "/especialidades/**", "/enfermedades/**", "/tratamientos/**", "/estado-citas/**", "/tipo-citas/**")
                            .hasAuthority("clinica:admin");

                    // --- Regla final ---
                    auth.anyRequest().authenticated();
                })

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

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