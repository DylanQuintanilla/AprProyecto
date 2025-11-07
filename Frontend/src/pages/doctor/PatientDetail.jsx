// src/pages/doctor/PatientDetail.jsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import api from '../../api/api';
import { useToast } from '../../hooks/useToast';

export default function PatientDetail() {
    const { id } = useParams(); // ID del paciente desde la URL
    const { showToast } = useToast();
    const [patient, setPatient] = useState(null);
    const [antecedentes, setAntecedentes] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchData = async () => {
        setLoading(true);
        try {
            // 1. Obtener perfil del paciente
            const patientResponse = await api.get(`/pacientes/${id}`);
            setPatient(patientResponse.data);

            // 2. Obtener antecedentes del paciente (asume endpoint de búsqueda por ID)
            // Nota: Este endpoint no existe explícitamente en el backend, 
            // pero el Doctor podría usar /antecedentes-medicos y filtrar por paciente ID 
            // o se crea un endpoint específico en el backend. Aquí usamos la lista general y filtramos.
            const antecedenteResponse = await api.get(`/antecedentes-medicos`); 
            const filtered = antecedenteResponse.data.filter(a => a.paciente.id === Number(id));
            setAntecedentes(filtered);

        } catch (error) {
            console.error("Error al cargar detalles del paciente:", error);
            showToast('Error al cargar la información del paciente.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [id]);

    if (loading) return <p className="text-center mt-8">Cargando detalles...</p>;
    if (!patient) return <p className="text-center mt-8">Paciente no encontrado.</p>;

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Perfil de {patient.nombre} {patient.apellido}</h1>

                <div className="bg-white p-6 rounded-lg shadow-md mb-8">
                    <h2 className="text-xl font-semibold mb-4">Información Personal</h2>
                    <p><strong>DUI:</strong> {patient.dui}</p>
                    <p><strong>Email:</strong> {patient.email}</p>
                    {/* ... más detalles del paciente */}
                </div>

                <div className="bg-white p-6 rounded-lg shadow-md">
                    <h2 className="text-xl font-semibold mb-4">Antecedentes Médicos</h2>
                    {/* Aquí iría el formulario para AGREGAR un nuevo antecedente (POST /antecedentes-medicos) */}
                    {antecedentes.length === 0 ? (
                        <p>No hay antecedentes registrados.</p>
                    ) : (
                        <ul className="space-y-4">
                            {antecedentes.map(a => (
                                <li key={a.id} className="border-l-4 border-blue-500 pl-4 py-2 bg-gray-50">
                                    <p className="font-semibold">{a.fechaRegistro.split(' ')[0]}</p>
                                    <p>{a.descripcion}</p>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
}