import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const PrivateRoute = ({ children, roles }) => {
    const { isAuthenticated, loading, hasAnyRole } = useAuth();

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (roles && !hasAnyRole(roles)) {
        return <Navigate to="/" replace />;
    }

    return children;
};

export default PrivateRoute;