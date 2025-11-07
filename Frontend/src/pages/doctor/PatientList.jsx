// src/pages/doctor/PatientList.jsx
import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../auth/AuthContext';
import Navbar from '../../components/Navbar';
import api from '../../api/api';
import { useToast } from '../../hooks/useToast';
import { Link } from 'react-router-dom';

const $ = window.jQuery;

export default function PatientList() {
    const { auth } = useAuth();
    const { showToast } = useToast();
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const tableRef = useRef(null);

    const fetchPatients = async () => {
        setLoading(true);
        try {
            // Llama a /pacientes. El backend usa el permiso para filtrar si es necesario.
            const response = await api.get('/pacientes'); 
            setPatients(response.data);
        } catch (error) {
            console.error("Error al obtener pacientes:", error);
            showToast('Error al cargar la lista de pacientes.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (auth.isLoggedIn) {
            fetchPatients();
        }
    }, [auth.isLoggedIn]);

    // Inicialización de DataTables
    useEffect(() => {
        if (!loading && patients.length > 0 && tableRef.current && window.jQuery) { 
            if ($.fn.DataTable.isDataTable(tableRef.current)) {
                $(tableRef.current).DataTable().destroy();
            }
            $(tableRef.current).DataTable({
                language: {
                    url: 'https://cdn.datatables.net/plug-ins/2.0.8/i18n/es-ES.json',
                },
                paging: true,
                searching: true,
                info: true,
                responsive: true,
            });
        }
        return () => {
            if (tableRef.current && window.jQuery && $.fn.DataTable.isDataTable(tableRef.current)) {
                $(tableRef.current).DataTable().destroy();
            }
        };
    }, [patients, loading]);

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Lista de Pacientes</h1>
                
                {loading && <p className="text-center text-blue-500">Cargando pacientes...</p>}
                
                {!loading && patients.length === 0 && <p className="text-center text-gray-500">No hay pacientes para mostrar.</p>}

                {!loading && patients.length > 0 && (
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <table ref={tableRef} className="display w-full border-collapse border border-gray-300">
                            <thead className="bg-gray-200">
                                <tr>
                                    <th className="p-2 border">ID</th>
                                    <th className="p-2 border">Nombre Completo</th>
                                    <th className="p-2 border">Email</th>
                                    <th className="p-2 border">DUI</th>
                                    <th className="p-2 border">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {patients.map((patient) => (
                                    <tr key={patient.id} className="hover:bg-gray-100">
                                        <td className="p-2 border">{patient.id}</td>
                                        <td className="p-2 border">{patient.nombre} {patient.apellido}</td>
                                        <td className="p-2 border">{patient.email}</td>
                                        <td className="p-2 border">{patient.dui}</td>
                                        <td className="p-2 border">
                                            <Link to={`/patients/${patient.id}`} className="text-blue-500 hover:underline">
                                                Ver Perfil
                                            </Link>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}