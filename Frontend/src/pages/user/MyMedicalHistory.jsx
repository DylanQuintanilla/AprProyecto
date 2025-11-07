// src/pages/user/MyMedicalHistory.jsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthContext';
import Navbar from '../../components/Navbar';
import api from '../../api/api';
import { useToast } from '../../hooks/useToast';

export default function MyMedicalHistory() {
    const { auth } = useAuth();
    const { showToast } = useToast();
    const [antecedentes, setAntecedentes] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchAntecedentes = async () => {
        setLoading(true);
        try {
            // Llama a /antecedentes-medicos. El backend debe filtrar automáticamente
            // por el usuario logueado (Paciente) usando el permiso :propios.
            const response = await api.get('/antecedentes-medicos'); 
            setAntecedentes(response.data);
        } catch (error) {
            console.error("Error al obtener antecedentes:", error);
            showToast('Error al cargar sus antecedentes médicos.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (auth.isLoggedIn) {
            fetchAntecedentes();
        }
    }, [auth.isLoggedIn]);

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Mis Antecedentes Médicos</h1>

                {loading && <p className="text-center text-blue-500">Cargando historial...</p>}
                
                {!loading && antecedentes.length === 0 && <p className="text-center text-gray-500">No tiene antecedentes médicos registrados.</p>}

                {!loading && antecedentes.length > 0 && (
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <ul className="space-y-4">
                            {antecedentes.map(a => (
                                <li key={a.id} className="border-l-4 border-teal-500 pl-4 py-2 bg-gray-50">
                                    <p className="text-sm text-gray-500">{a.fechaRegistro}</p>
                                    <p className="mt-1">{a.descripcion}</p>
                                </li>
                            ))}
                        </ul>
                    </div>
                )}
            </div>
        </div>
    );
}