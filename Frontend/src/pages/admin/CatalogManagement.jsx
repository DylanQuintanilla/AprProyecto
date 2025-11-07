// src/pages/admin/CatalogManagement.jsx
import React from 'react';
import Navbar from '../../components/Navbar';
import { Link } from 'react-router-dom';

export default function CatalogManagement() {
    // Los catálogos se gestionan con permisos de 'clinica:admin'
    const catalogs = [
        { name: "Consultorios", endpoint: "/consultorios" },
        { name: "Especialidades", endpoint: "/especialidades" },
        { name: "Enfermedades", endpoint: "/enfermedades" },
        { name: "Tipos de Cita", endpoint: "/tipo-citas" },
        { name: "Estados de Cita", endpoint: "/estado-citas" },
        { name: "Tratamientos", endpoint: "/tratamientos" },
    ];

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Gestión de Catálogos de Clínica (ADMIN)</h1>
                <p className="text-gray-600 mb-6">Administrar tablas auxiliares: Consultorios, Especialidades, etc.</p>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {catalogs.map((catalog) => (
                        <div key={catalog.name} className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition">
                            <h2 className="text-xl font-semibold mb-2">{catalog.name}</h2>
                            <p className="text-gray-500 mb-4">Endpoint: <code>{catalog.endpoint}</code></p>
                            <Link to={`/admin/catalog${catalog.endpoint}`} className="text-blue-500 hover:underline font-medium">
                                Gestionar {catalog.name} →
                            </Link>
                        </div>
                    ))}
                </div>

                {/* NOTA: Cada link debe llevar a una vista de CRUD específica o a una vista modular de tabla. */}
                <div className="mt-8">
                    <p className="text-sm text-gray-500">Para una implementación completa, cada enlace de 'Gestionar' necesitaría una vista o componente modular que realice el CRUD.</p>
                </div>
            </div>
        </div>
    );
}