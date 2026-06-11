import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const PrivateRoute = ({ children, roles }) => {
    const { isAuthenticated, loading, hasAnyRole } = useAuth();

    console.log('PrivateRoute check:', { isAuthenticated, loading, roles });

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (!isAuthenticated) {
        console.log('Not authenticated, redirecting to login');
        return <Navigate to="/login" replace />;
    }

    if (roles && !hasAnyRole(roles)) {
        console.log('No required role, redirecting to home');
        return <Navigate to="/" replace />;
    }

    return children;
};

export default PrivateRoute;