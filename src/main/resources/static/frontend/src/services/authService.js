import api from './api';

export const authService = {
    login: (email, password) => {
        console.log('Login attempt:', email);
        return api.post('/api/auth/login', { email, password });
    },
    register: (data) => {
        console.log('Register attempt:', data.email);
        return api.post('/api/auth/register', data);
    },
    getCurrentUser: () => {
        console.log('Getting current user');
        return api.get('/api/auth/me');
    }
};