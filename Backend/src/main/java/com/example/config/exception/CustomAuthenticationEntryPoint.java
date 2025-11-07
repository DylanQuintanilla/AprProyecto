package com.example.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // --- CORRECCIÓN DE URI (PARA QUE NO MUESTRE /error) ---
        String originalUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        if (originalUri == null) {
            originalUri = request.getRequestURI();
        }
        // --- FIN DE CORRECCIÓN ---

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .type(authException.getClass().getSimpleName())
                .title("Autenticación fallida") // Español
                .description("Se requiere autenticación completa para acceder a este recurso.") // Español
                .source(originalUri) // Usamos la URI corregida
                .build();

        ApiErrorWrapper errorWrapper = new ApiErrorWrapper();
        errorWrapper.addApiError(apiError);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorWrapper);
    }
}