// src/pages/shared/ProfilePage.jsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../auth/AuthContext';
import Navbar from '../../components/Navbar';
import api from '../../api/api';
import { useToast } from '../../hooks/useToast';

export default function ProfilePage() {
    const { auth } = useAuth();
    const { showToast } = useToast();
    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);

    // Determinar si es Paciente o Dentista (para llamar al endpoint correcto)
    const isPatient = auth.permissions.has('citas:leer:propias'); 
    const isDoctor = auth.permissions.has('citas:leer:asignadas');
    const endpointBase = isPatient ? '/pacientes' : (isDoctor ? '/dentistas' : '');
    
    // 1. Obtener los datos del perfil del usuario logueado
    // Nota: El backend necesitaría un endpoint como /pacientes/me o /dentistas/me
    const fetchProfile = async () => {
        if (!endpointBase) return;
        setLoading(true);
        try {
            // Buscamos el perfil por el username logueado. 
            // Esto asume que el backend tiene endpoints como /pacientes?username={user}
            // Pero, como el ID es más seguro, usaremos un ID si lo tuviéramos
            // Por ahora, asumiremos que /profile existe o que llamamos por ID
            
            // Usaremos el endpoint general y confiaremos en que el backend filtre por auth.
            const response = await api.get(`${endpointBase}`); 

            // Para simplificar: asumimos que solo obtenemos 1 perfil para el usuario logueado
            // En un caso real, el backend debería devolver solo el perfil propio.
            setProfileData(response.data[0] || response.data); 
        } catch (error) {
            console.error('Error al cargar el perfil:', error);
            showToast('Error al cargar la información del perfil.', 'error');
        } finally {
            setLoading(false);
        }
    };

    // 2. Manejar la actualización del perfil
    const handleUpdate = async (e) => {
        e.preventDefault();
        // ID es necesario para el PUT
        const profileId = profileData?.id; 
        if (!profileId) return;

        try {
            const updateData = {
                // Solo enviamos los campos que se pueden actualizar (nombre, apellido, email)
                nombre: profileData.nombre,
                apellido: profileData.apellido,
                email: profileData.email,
                numeroTelefono: profileData.numeroTelefono,
                // Si es paciente, enviamos DUI y fechaNacimiento
                ...(isPatient && { 
                    dui: profileData.dui, 
                    fechaNacimiento: profileData.fechaNacimiento 
                }),
                // Importante: No enviar usuario/contraseña aquí
            };
            
            await api.put(`${endpointBase}/${profileId}`, updateData);
            showToast('Perfil actualizado con éxito.', 'success');
            setIsEditing(false);
            fetchProfile(); // Recargar datos
            
        } catch (error) {
            console.error('Error al actualizar el perfil:', error.response?.data);
            showToast('Error al actualizar: ' + (error.response?.data?.errors?.[0]?.description || 'Intente de nuevo.'), 'error');
        }
    };

    useEffect(() => {
        if (auth.isLoggedIn) {
            fetchProfile();
        }
    }, [auth.isLoggedIn]);

    if (loading) return <p className="text-center mt-8">Cargando perfil...</p>;
    if (!profileData) return <p className="text-center mt-8">No se encontró información de perfil.</p>;

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold mb-6">Mi Perfil</h1>

                <div className="bg-white p-6 rounded-lg shadow-md max-w-lg mx-auto">
                    
                    {/* Botón de Edición */}
                    <div className="flex justify-end mb-4">
                        <button 
                            onClick={() => setIsEditing(!isEditing)}
                            className="text-blue-500 hover:text-blue-700 font-semibold"
                        >
                            {isEditing ? 'Cancelar Edición' : 'Editar Información'}
                        </button>
                    </div>

                    <form onSubmit={handleUpdate}>
                        {/* Campo Nombre */}
                        <div className="mb-4">
                            <label className="block text-gray-700">Nombre</label>
                            <input
                                type="text"
                                value={profileData.nombre}
                                onChange={(e) => setProfileData({...profileData, nombre: e.target.value})}
                                disabled={!isEditing}
                                required
                                className="w-full mt-1 p-2 border rounded-md disabled:bg-gray-50"
                            />
                        </div>
                        
                        {/* Más campos (Apellido, Email, Teléfono, etc.) */}
                        {/* ... (Continuar con los campos) ... */}

                        {isEditing && (
                            <button 
                                type="submit" 
                                className="w-full bg-green-500 text-white p-2 rounded-md hover:bg-green-600 mt-4"
                            >
                                Guardar Cambios
                            </button>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
}