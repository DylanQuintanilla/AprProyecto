// src/pages/shared/ChangePassword.jsx (Ejemplo de plantilla)
import React from 'react';
import Navbar from '../../components/Navbar';

export default function ChangePassword() {
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Cambiar Contraseña</h1>
                <p>Formulario para cambiar contraseña aquí.</p>
            </div>
        </div>
    );
}