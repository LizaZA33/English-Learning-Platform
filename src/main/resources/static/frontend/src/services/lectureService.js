import api from './api';

export const lectureService = {
    getAll: (page = 0, size = 10) => {
        return api.get('/api/lectures', { params: { page, size } });
    },
    getByModule: (moduleId, page = 0, size = 10) => {
        return api.get(`/api/lectures/module/${moduleId}`, { params: { page, size } });
    },
    getById: (id) => {
        return api.get(`/api/lectures/${id}`);
    },
    create: (data) => {
        return api.post('/api/lectures', data);
    },
    search: (title, page = 0, size = 10) => {
        return api.get('/api/lectures/search', { params: { title, page, size } });
    }
};