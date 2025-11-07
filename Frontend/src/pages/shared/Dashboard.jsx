// src/pages/Dashboard.jsx
import React from 'react';
import { useAuth } from '../../auth/AuthContext';
import Navbar from '../../components/Navbar';
import { Link } from 'react-router-dom';

export default function Dashboard() {
    const { auth } = useAuth();

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-4">Bienvenido, {auth.username}</h1>
                <p className="mb-6 text-gray-600">
                    Tu sesión está activa.
                </p>
                
                <div className="bg-white p-6 rounded-lg shadow-md mb-8">
                    <h2 className="text-xl font-semibold mb-3">Accesos Rápidos</h2>
                    <nav className="flex space-x-4">
                        <Link to="/citas" className="text-blue-500 hover:underline">Gestionar Citas</Link>
                        {/* Ejemplo de control de acceso por permisos (si tienes permisos 'pacientes:leer:lista') */}
                        {auth.permissions.has('pacientes:leer:lista') && (
                            <Link to="/pacientes" className="text-green-500 hover:underline">Ver Pacientes</Link>
                        )}
                    </nav>
                </div>
            </div>
        </div>
    );
}