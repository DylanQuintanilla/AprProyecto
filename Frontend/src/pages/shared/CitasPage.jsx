// src/pages/CitasPage.jsx

import React, { useEffect, useRef, useState } from 'react';
import api from '../../api/api'; 
import { useAuth } from '../../auth/AuthContext';
import Navbar from '../../components/Navbar';
// Se utiliza la variable global jQuery cargada por CDN
const $ = window.jQuery;

export default function CitasPage() {
    const tableRef = useRef(null);
    const { auth } = useAuth();
    const [citas, setCitas] = useState([]);
    const [loading, setLoading] = useState(true);

    // Función para obtener datos
    const fetchCitas = async () => {
        if (!auth.accessToken) return;
        setLoading(true);

        try {
            // Usar api.get: El interceptor de request en api.js ya adjunta el token.
            const response = await api.get('/citas'); 
            
            setCitas(response.data);
        } catch (error) {
            console.error("Error al obtener citas:", error);
            // Si el 401 se dispara, el interceptor intenta refrescar.
            if (error.response && error.response.status === 403) {
                // Puedes agregar aquí una lógica específica si recibes un 403 (Permiso Denegado)
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (auth.isLoggedIn) {
            fetchCitas();
        }
    }, [auth.isLoggedIn, auth.accessToken]);

    // Inicialización de DataTables
    useEffect(() => {
        // Solo inicializamos si hay datos, jQuery está cargado y ya se terminó la carga.
        if (!loading && citas.length > 0 && tableRef.current && window.jQuery) { 
            
            // 1. Destruir la instancia anterior si existe
            if ($.fn.DataTable.isDataTable(tableRef.current)) {
                $(tableRef.current).DataTable().destroy();
            }

            // 2. Inicializar DataTables
            $(tableRef.current).DataTable({
                language: {
                    url: 'https://cdn.datatables.net/plug-ins/2.0.8/i18n/es-ES.json', // Español
                },
                paging: true,
                searching: true,
                info: true,
                responsive: true,
            });
        }
     
        // 3. Función de limpieza
        return () => {
            if (tableRef.current && window.jQuery && $.fn.DataTable.isDataTable(tableRef.current)) {
                $(tableRef.current).DataTable().destroy();
            }
        };
    }, [citas, loading]); 

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Listado de Citas</h1>
                
                {loading && <p className="text-center text-blue-500">Cargando citas...</p>}
                
                {!loading && citas.length === 0 && <p className="text-center text-gray-500">No hay citas para mostrar.</p>}

                {!loading && citas.length > 0 && (
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <table ref={tableRef} className="display w-full border-collapse border border-gray-300">
                            <thead className="bg-gray-200">
                                <tr>
                                    <th className="p-2 border">ID</th>
                                    <th className="p-2 border">Paciente</th>
                                    <th className="p-2 border">Dentista</th>
                                    <th className="p-2 border">Fecha</th>
                                    <th className="p-2 border">Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                {citas.map((cita) => (
                                    <tr key={cita.id} className="hover:bg-gray-100">
                                        <td className="p-2 border">{cita.id}</td>
                                        <td className="p-2 border">{cita.paciente?.nombre} {cita.paciente?.apellido}</td>
                                        <td className="p-2 border">{cita.dentista?.nombre} {cita.dentista?.apellido}</td>
                                        <td className="p-2 border">{cita.fechaCita}</td>
                                        <td className="p-2 border">{cita.estado?.nombre}</td>
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