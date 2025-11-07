// src/pages/user/AppointmentRequest.jsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthContext';
import Navbar from '../../components/Navbar';
import api from '../../api/api';
import { useToast } from '../../hooks/useToast';

export default function AppointmentRequest() {
    const { auth } = useAuth();
    const { showToast } = useToast();
    const [dentists, setDentists] = useState([]);
    const [appointmentData, setAppointmentData] = useState({
        dentistaId: '',
        fechaCita: '', // Formato yyyy-MM-dd
        hora: '',       // Formato HH:mm
        motivo: '',
        estadoId: 1,  // Asumimos '1' es el ID para el estado 'Pendiente'
    });

    // 1. Cargar la lista de dentistas disponibles al inicio
    const fetchDentists = async () => {
        try {
            // El endpoint de dentistas debería ser público o tener un permiso de lectura
            const response = await api.get('/dentistas'); 
            setDentists(response.data);
        } catch (error) {
            console.error('Error al cargar dentistas:', error);
            showToast('No se pudo cargar la lista de dentistas.', 'error');
        }
    };

    useEffect(() => {
        fetchDentists();
    }, []);

    // 2. Manejar el envío del formulario de solicitud de cita
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Verificación de campos básicos (la validación detallada la hará Spring Boot)
        if (!appointmentData.dentistaId || !appointmentData.fechaCita || !appointmentData.hora) {
            showToast('Por favor, complete los campos obligatorios.', 'error');
            return;
        }

        try {
            // Los campos fechaCita y hora tienen el formato esperado por CitaRequest [cite: 371-372]
            const payload = {
                ...appointmentData,
                // El backend espera el ID del estado, que asumimos es '1' (Pendiente)
                estadoId: 1, 
            };

            await api.post('/citas', payload); // Endpoint de creación 
            showToast('Cita solicitada con éxito. Esperando confirmación.', 'success');
            
            // Limpiar formulario o redirigir
            setAppointmentData({
                dentistaId: '',
                fechaCita: '',
                hora: '',
                motivo: '',
                estadoId: 1,
            });

        } catch (error) {
            console.error('Error al solicitar cita:', error.response?.data);
            showToast('Error al solicitar cita: ' + (error.response?.data?.errors?.[0]?.description || 'Intente de nuevo.'), 'error');
        }
    };

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Solicitar Nueva Cita</h1>
                
                <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md max-w-lg mx-auto">
                    
                    {/* Campo 1: Dentista */}
                    <div className="mb-4">
                        <label className="block text-gray-700 mb-1">Dentista</label>
                        <select
                            value={appointmentData.dentistaId}
                            onChange={(e) => setAppointmentData({...appointmentData, dentistaId: e.target.value})}
                            required
                            className="w-full p-2 border rounded-md"
                        >
                            <option value="">Seleccione un Dentista</option>
                            {dentists.map(dentist => (
                                <option key={dentist.id} value={dentist.id}>
                                    {dentist.nombre} {dentist.apellido}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Campo 2: Fecha */}
                    <div className="mb-4">
                        <label className="block text-gray-700 mb-1">Fecha de la Cita</label>
                        <input
                            type="date"
                            value={appointmentData.fechaCita}
                            onChange={(e) => setAppointmentData({...appointmentData, fechaCita: e.target.value})}
                            required
                            className="w-full p-2 border rounded-md"
                            min={new Date().toISOString().split('T')[0]} // No permitir fechas pasadas
                        />
                    </div>

                    {/* Campo 3: Hora */}
                    <div className="mb-4">
                        <label className="block text-gray-700 mb-1">Hora (ej. 10:30)</label>
                        <input
                            type="time"
                            value={appointmentData.hora}
                            onChange={(e) => setAppointmentData({...appointmentData, hora: e.target.value})}
                            required
                            className="w-full p-2 border rounded-md"
                        />
                    </div>
                    
                    {/* Campo 4: Motivo */}
                    <div className="mb-4">
                        <label className="block text-gray-700 mb-1">Motivo de la Cita (Opcional)</label>
                        <textarea
                            value={appointmentData.motivo}
                            onChange={(e) => setAppointmentData({...appointmentData, motivo: e.target.value})}
                            rows="3"
                            className="w-full p-2 border rounded-md"
                        />
                    </div>

                    <button 
                        type="submit" 
                        className="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 mt-4"
                    >
                        Solicitar Cita
                    </button>
                </form>
            </div>
        </div>
    );
}