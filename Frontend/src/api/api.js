// src/api/api.js

import axios from 'axios';

// 1. Crear la instancia base de Axios
const api = axios.create({
    baseURL: 'http://localhost:8080', 
    headers: {
        'Content-Type': 'application/json',
    },
});

// Variables para manejar el refresco de tokens
let isRefreshing = false;
let failedQueue = [];

// Función para procesar la cola de peticiones fallidas
const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// 2. Interceptor de Peticiones (Adjunta el Access Token)
api.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem('accessToken');
        
        // Si existe un token, lo adjuntamos a todas las peticiones
        if (accessToken) {
            config.headers['Authorization'] = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 3. Interceptor de Respuestas (Maneja el 401 y Refresh Token)
api.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        // Si es un error sin respuesta, o una petición ya reintentada, o no es 401, lo rechazamos
        if (!error.response || originalRequest._retry || error.response.status !== 401) {
            return Promise.reject(error);
        }

        const refreshToken = localStorage.getItem('refreshToken');
        
        // Si no hay Refresh Token, no podemos refrescar, forzamos el error.
        if (!refreshToken) {
            // Nota: Aquí se debería forzar el logout/redirección en el componente.
            return Promise.reject(error);
        }

        // Si ya estamos refrescando, añadimos la petición a la cola y esperamos
        if (isRefreshing) {
            return new Promise(function(resolve, reject) {
                failedQueue.push({ resolve, reject });
            }).then(token => {
                originalRequest.headers['Authorization'] = 'Bearer ' + token;
                return api(originalRequest);
            }).catch(err => {
                return Promise.reject(err);
            });
        }
        
        // Empezar el proceso de refresco
        isRefreshing = true;
        originalRequest._retry = true; // Marca para evitar bucle

        try {
            // Llama al endpoint de refresco del backend
            const refreshResponse = await axios.post('http://localhost:8080/auth/refresh', { refreshToken });
            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = refreshResponse.data;

            // 1. Almacenar los nuevos tokens
            localStorage.setItem('accessToken', newAccessToken);
            localStorage.setItem('refreshToken', newRefreshToken);

            // 2. Actualizar el encabezado de la petición original
            originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

            // 3. Procesar todas las peticiones en cola
            processQueue(null, newAccessToken);
            
            // 4. Re-ejecutar la petición original
            return api(originalRequest);

        } catch (refreshError) {
            // Error al refrescar (Refresh Token expiró o es inválido)
            console.error("Error al refrescar el token. Forzando posible Logout/Redirección.");
            localStorage.clear();
            processQueue(refreshError, null);
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

export default api;