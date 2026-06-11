import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';
import { userService } from '../services/userService';

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
    const [authError, setAuthError] = useState(null);

    const loadCurrentUser = async (retryCount = 0) => {
        const token = localStorage.getItem('token');
        console.log('loadCurrentUser - token exists:', !!token);
        
        if (!token) {
            console.log('No token found, setting loading to false');
            setLoading(false);
            return;
        }
        
        try {
            console.log('Fetching current user from /api/users/me');
            const response = await userService.getCurrentUser();
            console.log('User loaded successfully:', response.data);
            setUser(response.data);
            setAuthError(null);
            return response.data;
        } catch (error) {
            console.error('Error loading user:', error);
            console.error('Error status:', error.response?.status);
            console.error('Error message:', error.response?.data?.message);
            if (error.response?.status === 401 || error.response?.status === 403) {
                console.log('Token invalid, removing from localStorage');
                localStorage.removeItem('token');
                setUser(null);
                setAuthError('Сессия истекла. Пожалуйста, войдите снова.');
            } else if (retryCount < 2) {
                console.log(`Retrying loadCurrentUser (${retryCount + 1}/2)...`);
                await new Promise(resolve => setTimeout(resolve, 1000));
                return loadCurrentUser(retryCount + 1);
            } else {
                setAuthError('Ошибка загрузки профиля. Пожалуйста, обновите страницу.');
            }
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCurrentUser();
    }, []);

    const login = async (email, password) => {
        console.log('Login attempt for:', email);
        setLoading(true);
        setAuthError(null);
        
        try {
            const response = await authService.login(email, password);
            console.log('Login response status:', response.status);
            
            const { token } = response.data;
            
            if (!token) {
                throw new Error('Token not received from server');
            }
            
            console.log('Token received, saving to localStorage');
            localStorage.setItem('token', token);
            
            const userData = await loadCurrentUser();
            
            if (!userData) {
                throw new Error('Failed to load user data after login');
            }
            
            console.log('Login successful for:', userData.email);
            return response;
        } catch (error) {
            console.error('Login error:', error);
            console.error('Error status:', error.response?.status);
            console.error('Error data:', error.response?.data);
            localStorage.removeItem('token');
            setUser(null);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const register = async (data) => {
        console.log('Register attempt for:', data.email);
        setLoading(true);
        setAuthError(null);
        
        try {
            const response = await authService.register(data);
            console.log('Register response status:', response.status);
            
            const { token } = response.data;
            
            if (!token) {
                throw new Error('Token not received from server');
            }
            
            console.log('Token received, saving to localStorage');
            localStorage.setItem('token', token);
            
            const userData = await loadCurrentUser();
            
            if (!userData) {
                throw new Error('Failed to load user data after registration');
            }
            
            console.log('Registration successful for:', userData.email);
            return response;
        } catch (error) {
            console.error('Registration error:', error);
            localStorage.removeItem('token');
            setUser(null);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        console.log('Logging out');
        localStorage.removeItem('token');
        setUser(null);
        setAuthError(null);
    };

    const refreshUser = async () => {
        console.log('Refreshing user data');
        setLoading(true);
        await loadCurrentUser();
        setLoading(false);
    };

    const hasRole = (role) => {
        const has = user?.roles?.includes(role) || false;
        console.log(`hasRole(${role}):`, has, 'user roles:', user?.roles);
        return has;
    };

    const hasAnyRole = (roles) => {
        if (!user?.roles) return false;
        const has = roles.some(role => user.roles.includes(role));
        console.log(`hasAnyRole(${roles}):`, has, 'user roles:', user?.roles);
        return has;
    };

    const value = {
        user,
        loading,
        authError,
        login,
        register,
        logout,
        refreshUser,
        hasRole,
        hasAnyRole,
        isAuthenticated: !!user && !loading
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};