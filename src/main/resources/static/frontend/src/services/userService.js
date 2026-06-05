import api from './api';

export const userService = {
    getAll: (search, page = 0, size = 20) => {
        return api.get('/api/users', { params: { search, page, size } });
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
        return api.post('/api/students/profile', data);
    },
    createTeacherProfile: (data) => {
        return api.post('/api/teachers/profile', data);
    },
    updateProfile: (data) => {
        return api.put('/api/users/profile', data);
    }
};