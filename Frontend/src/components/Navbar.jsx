// src/components/Navbar.jsx
import React from 'react';
import { useAuth } from '../auth/AuthContext';
import { Link } from 'react-router-dom';

export default function Navbar() {
    const { auth, logout, hasPermission } = useAuth();

    // --- Permisos Relevantes ---
    const isPatient = auth.isLoggedIn && hasPermission('citas:solicitar:propias');
    const isDoctor = auth.isLoggedIn && hasPermission('pacientes:leer:lista');
    const isAdmin = auth.isLoggedIn && hasPermission('clinica:admin');

    // --- Enlaces Condicionales ---
    const getNavLinks = () => {
        const links = [];

        // 1. Enlaces Compartidos (para todos los logueados)
        links.push(<Link key="citas" to="/citas" className="hover:text-blue-200">Mis Citas</Link>);
        links.push(<Link key="profile" to="/profile" className="hover:text-blue-200">Perfil</Link>);
        
        // 2. Enlaces para Pacientes
        if (isPatient) {
            links.push(<Link key="request" to="/request-appointment" className="hover:text-blue-200">Solicitar Cita</Link>);
            links.push(<Link key="history" to="/my-medical-history" className="hover:text-blue-200">Historial Médico</Link>);
        }

        // 3. Enlaces para Doctores (Gestionar Pacientes)
        // NOTA: Si el Doctor tiene permisos de Paciente (como paciente/doctor), verá ambos sets.
        if (isDoctor) {
            links.push(<Link key="patients" to="/patients" className="hover:text-blue-200">Pacientes</Link>);
            // Asumimos que "Mis Citas" ya cubre las asignadas al doctor
        }

        // 4. Enlaces para Administradores
        if (isAdmin) {
            // Utilizamos el separador visual para Admin
            links.push(<span key="sep" className="text-blue-200">|</span>); 
            links.push(<Link key="dentists" to="/admin/dentists" className="hover:text-red-200 font-bold">Adm. Dentistas</Link>);
            links.push(<Link key="catalog" to="/admin/catalog" className="hover:text-red-200 font-bold">Adm. Catálogos</Link>);
        }

        return links;
    };

    return (
        <header className="bg-blue-600 text-white shadow-lg">
            <div className="container mx-auto flex justify-between items-center p-4">
                <div className="text-xl font-bold">
                    <Link to="/">Clínica Dental</Link>
                </div>
              
                <nav>
                    {auth.isLoggedIn ? (
                        <div className="flex space-x-4 items-center">
                            
                            {/* Renderizar Enlaces Dinámicos */}
                            {getNavLinks().map((link, index) => (
                                <React.Fragment key={index}>{link}</React.Fragment>
                            ))}

                            <span className="text-blue-200">|</span>
                            <span className="text-sm font-semibold">Hola, {auth.username}</span>
                            <button
                                onClick={logout}
                                className="bg-red-500 hover:bg-red-600 text-white py-1 px-3 rounded text-sm"
                            >
                                Cerrar Sesión
                            </button>
                        </div>
                    ) : (
                        <Link to="/login" className="hover:text-blue-200">Iniciar Sesión</Link>
                    )}
                </nav>
            </div>
        </header>
    );
}