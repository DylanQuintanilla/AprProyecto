import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/api'; // <--- CAMBIO: Importar la instancia de Axios

const API_URL = 'http://localhost:8080/auth'; // Ya no es tan necesario, pero se mantiene
const AuthContext = createContext();

const decodeJwt = (token) => {
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(decoded);
    } catch (error) {
        console.error("Error decodificando token:", error);
        return null;
    }
};

export const AuthProvider = ({ children }) => {
    const [auth, setAuth] = useState({
        isLoggedIn: false,
        username: null,
        permissions: new Set(),
        accessToken: null,
        refreshToken: null
    });
    
    useEffect(() => {
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = localStorage.getItem('refreshToken');
        const username = localStorage.getItem('username');

        if (accessToken && refreshToken && username) {
            const decoded = decodeJwt(accessToken);
            if (decoded && decoded.authorities) {
                setAuth({
                    isLoggedIn: true, 
                    username,
                    permissions: new Set(decoded.authorities.split(',')),
                    accessToken,
                    refreshToken 
                });
            }
        }
    }, []);

    // Función de LOGIN (Llama al backend usando API/Axios)
    const login = async (username, password) => {
        try {
            // --- CAMBIO: Usar api.post en lugar de fetch ---
            const response = await api.post('/auth/log-in', { username, password });
            
            const data = response.data; // Axios retorna la respuesta JSON en .data
            const { accessToken, refreshToken, username: user, status } = data;

            if (status) {
                const decoded = decodeJwt(accessToken);
                const permissionsArray = decoded.authorities ? decoded.authorities.split(',') : [];

                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
                localStorage.setItem('username', user);
                
                setAuth({
                    isLoggedIn: true,
                    username: user,
                    permissions: new Set(permissionsArray),
                    accessToken,
                    refreshToken 
                });
                return true; 
            }
            return false;
        } catch (error) {
            // Manejo unificado de errores de Axios
            if (error.response) {
                console.error("Error en Login (Backend - Axios):", error.response.data);
            } else {
                console.error("Error en login (Red o Infraestructura - Axios):", error);
            }
            return false;
        }
    };

    const logout = () => {
        localStorage.clear(); 
        setAuth({
            isLoggedIn: false,
            username: null,
            permissions: new Set(),
            accessToken: null,
            refreshToken: null
        });
    };

    const hasPermission = (permission) => auth.permissions.has(permission) || auth.permissions.has('ADMIN'); 

    return (
        <AuthContext.Provider value={{ auth, login, logout, hasPermission }}>
            {children}
        </AuthContext.Provider>
    ); 
};

export const useAuth = () => useContext(AuthContext);