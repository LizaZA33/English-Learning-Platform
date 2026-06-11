import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const RoleBasedRedirect = () => {
    const { isAuthenticated, loading, hasRole, user } = useAuth();
    
    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }
    
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }
    
    // Админ сразу на админ-панель
    if (hasRole('ADMIN')) {
        return <Navigate to="/admin" replace />;
    }
    
    // Остальные на флешкарты
    return <Navigate to="/flashcards" replace />;
};

export default RoleBasedRedirect;