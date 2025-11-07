// src/auth/PermittedRoute.jsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

/**
 * Componente que protege una ruta si el usuario no tiene los permisos necesarios.
 * @param {string[]} requiredPermissions - Lista de permisos necesarios (OR lógico)
 * @param {React.Component} element - El componente a renderizar
 */
const PermittedRoute = ({ requiredPermissions = [], element: Element }) => {
    const { auth, hasPermission } = useAuth();
    
    // 1. Check if authenticated (if not, redirect to login)
    if (!auth.isLoggedIn) {
        return <Navigate to="/login" />;
    }

    // 2. Check for required permissions
    const isPermitted = requiredPermissions.some(permission => hasPermission(permission));

    // Si no se requieren permisos O si el usuario tiene alguno de los permisos
    if (requiredPermissions.length === 0 || isPermitted) {
        return <Element />;
    }
    
    // 3. If authenticated but not permitted, show 403 or redirect to dashboard
    // Podrías crear una vista 403, pero por ahora redirigimos al inicio.
    return <Navigate to="/" />; 
};

export default PermittedRoute;