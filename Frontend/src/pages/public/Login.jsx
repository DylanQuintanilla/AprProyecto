// src/pages/Login.jsx
import React, { useState } from 'react';
import { useAuth } from '../../auth/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../../hooks/useToast'; // Asegúrate de que la ruta sea correcta

export default function Login() {
    // 1. ESTADOS
    const [username, setUsername] = useState('paciente1'); 
    const [password, setPassword] = useState('1234');
    
    // 2. HOOKS DE CONTEXTO Y NAVEGACIÓN
    const { login } = useAuth();
    const navigate = useNavigate();
    const { showToast } = useToast();

    // 3. FUNCIÓN DE ENVÍO
    const handleSubmit = async (e) => {
        // Previene el comportamiento por defecto de recarga del formulario
        e.preventDefault(); 
        
        // Llamar a la lógica de autenticación
        const success = await login(username, password);
        
        if (success) {
            showToast('Inicio de sesión exitoso', 'success');
            navigate('/');
        } else {
            // Este toast debería aparecer incluso si el backend está caído o las credenciales son malas
            showToast('Credenciales inválidas o Error de conexión.', 'error'); 
        }
    };

    // 4. RENDERIZADO
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            {/* 5. EL HANDLER DEBE ESTAR ASIGNADO AL FORMULARIO */}
            <form onSubmit={handleSubmit} className="bg-white p-8 rounded-lg shadow-md w-96">
                <h2 className="text-2xl font-bold mb-6 text-center">Clínica Dental Login</h2>
                
                {/* Campos de input */}
                <div className="mb-4">
                    <label className="block text-gray-700">Usuario</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="w-full mt-1 p-2 border rounded-md"
                        required
                    />
                </div>
                <div className="mb-6">
                    <label className="block text-gray-700">Contraseña</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full mt-1 p-2 border rounded-md"
                        required
                    />
                </div>
                
                {/* El botón de tipo "submit" dentro del form dispara el onSubmit del formulario */}
                <button type="submit" className="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600">
                    Iniciar Sesión
                </button>
            </form>
        </div>
    );
}