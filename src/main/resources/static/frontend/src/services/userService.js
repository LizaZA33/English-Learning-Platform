
import api from './api';

export const userService = {
    getAll: (search, page = 0, size = 20) => {
        return api.get('/api/users', { params: { search, page, size } });
    },
    getCurrentUser: () => {
        console.log('userService.getCurrentUser called');
        return api.get('/api/users/me');
    },
    updateRoles: (userId, role, add) => {
        return api.put(`/api/users/${userId}/roles`, null, {
            params: { role, add }
        });
    },
    delete: (userId) => {
        return api.delete(`/api/users/${userId}`);
    },
    createStudentProfile: (data) => {
        return api.post('/api/users/students/profile', data);
    },
    createTeacherProfile: (data) => {
        return api.post('/api/users/teachers/profile', data);
    },
    updateProfile: (data) => {
        return api.put('/api/users/profile', data);
    }
};