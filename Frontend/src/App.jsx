// src/App.jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './auth/AuthContext.jsx'; // Nota: uso de .jsx explícito
import PermittedRoute from './auth/PermittedRoute.jsx'; // <--- NUEVA IMPORTACIÓN

// --- 1. IMPORTAR VISTAS DESDE SUS NUEVAS UBICACIONES ---
// Publicas
import Login from './pages/public/Login.jsx'; 
// Compartidas
import Dashboard from './pages/shared/Dashboard.jsx';
import CitasPage from './pages/shared/CitasPage.jsx'; 
import ProfilePage from './pages/shared/ProfilePage.jsx';
import ChangePassword from './pages/shared/ChangePassword.jsx';
// De Paciente (USER)
import MyMedicalHistory from './pages/user/MyMedicalHistory.jsx';
import AppointmentRequest from './pages/user/AppointmentRequest.jsx';
// De Doctor (DOCTOR)
import PatientList from './pages/doctor/PatientList.jsx';
import PatientDetail from './pages/doctor/PatientDetail.jsx';
// De Administrador (ADMIN)
import DentistManagement from './pages/admin/DentistManagement.jsx';
import CatalogManagement from './pages/admin/CatalogManagement.jsx';


// Componente para proteger rutas (solo requiere estar logueado, sin permisos específicos)
const PrivateRoute = ({ element: Element }) => {
    return <PermittedRoute requiredPermissions={[]} element={Element} />;
}; 

function AppRoutes() {
    return (
        <Routes>
            {/* 1. Ruta Pública */}
            <Route path="/login" element={<Login />} />
            
            {/* 2. Rutas Compartidas (Solo requieren estar logueado) */}
            <Route path="/" element={<PrivateRoute element={Dashboard} />} />
            <Route path="/citas" element={<PrivateRoute element={CitasPage} />} />
            <Route path="/profile" element={<PrivateRoute element={ProfilePage} />} />
            <Route path="/change-password" element={<PrivateRoute element={ChangePassword} />} />

            {/* 3. Rutas de Pacientes (USER) */}
            <Route 
                path="/request-appointment" 
                element={<PermittedRoute requiredPermissions={["citas:solicitar:propias"]} element={AppointmentRequest} />} 
            />
            <Route 
                path="/my-medical-history" 
                element={<PermittedRoute requiredPermissions={["antecedentes:leer:propios"]} element={MyMedicalHistory} />} 
            />

            {/* 4. Rutas de Personal Médico (DOCTOR) */}
            <Route 
                path="/patients" 
                element={<PermittedRoute requiredPermissions={["pacientes:leer:lista"]} element={PatientList} />} 
            />
            <Route 
                path="/patients/:id" 
                element={<PermittedRoute requiredPermissions={["pacientes:leer:perfil", "pacientes:admin"]} element={PatientDetail} />} 
            />

            {/* 5. Rutas de Administración (ADMIN) */}
            <Route 
                path="/admin/dentists" 
                element={<PermittedRoute requiredPermissions={["dentistas:admin"]} element={DentistManagement} />} 
            />
            <Route 
                path="/admin/catalog" 
                element={<PermittedRoute requiredPermissions={["clinica:admin"]} element={CatalogManagement} />} 
            />

            {/* Redirección para cualquier otra ruta no definida */}
            <Route path="*" element={<Navigate to="/" />} />
        </Routes>
    );
}

export default function App() {
    return (
        <Router>
            <AuthProvider>
                <AppRoutes />
            </AuthProvider>
        </Router>
    );
}