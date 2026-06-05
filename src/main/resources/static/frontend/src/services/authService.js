import api from './api';

export const authService = {
    login: (email, password) => {
        return api.post('/api/auth/login', { email, password });
    },
    register: (data) => {
        return api.post('/api/auth/register', data);
    },
    getCurrentUser: () => {
        return api.get('/api/auth/me');
    }
};