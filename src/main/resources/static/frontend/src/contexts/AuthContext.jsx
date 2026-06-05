import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            authService.getCurrentUser()
                .then(response => {
                    setUser(response.data);
                })
                .catch(() => {
                    localStorage.removeItem('token');
                })
                .finally(() => {
                    setLoading(false);
                });
        } else {
            setLoading(false);
        }
    }, []);

    const login = async (email, password) => {
        const response = await authService.login(email, password);
        localStorage.setItem('token', response.data.token);
        const userResponse = await authService.getCurrentUser();
        setUser(userResponse.data);
        return response;
    };

    const register = async (data) => {
        const response = await authService.register(data);
        localStorage.setItem('token', response.data.token);
        const userResponse = await authService.getCurrentUser();
        setUser(userResponse.data);
        return response;
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    const hasRole = (role) => {
        return user?.roles?.includes(role) || false;
    };

    const hasAnyRole = (roles) => {
        if (!user?.roles) return false;
        return roles.some(role => user.roles.includes(role));
    };

    const value = {
        user,
        loading,
        login,
        register,
        logout,
        hasRole,
        hasAnyRole,
        isAuthenticated: !!user
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};