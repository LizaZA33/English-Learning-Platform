import api from './api';

export const lessonService = {
    getMyLessons: (page = 0, size = 10) => {
        return api.get('/api/lessons/my', { params: { page, size } });
    },
    getTeacherLessons: (teacherId, page = 0, size = 10) => {
        return api.get(`/api/lessons/teacher/${teacherId}`, { params: { page, size } });
    },
    getById: (id) => {
        return api.get(`/api/lessons/${id}`);
    },
    create: (data) => {
        return api.post('/api/lessons', data);
    },
    update: (id, data) => {
        return api.put(`/api/lessons/${id}`, data);
    },
    delete: (id) => {
        return api.delete(`/api/lessons/${id}`);
    }
};