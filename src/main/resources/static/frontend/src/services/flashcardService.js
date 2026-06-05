import api from './api';

export const flashcardService = {
    getByLesson: (lessonId, page = 0, size = 20) => {
        return api.get(`/api/flashcards/lesson/${lessonId}`, {
            params: { page, size }
        });
    },
    getById: (id) => {
        return api.get(`/api/flashcards/${id}`);
    },
    create: (data) => {
        return api.post('/api/flashcards', data);
    },
    update: (id, data) => {
        return api.put(`/api/flashcards/${id}`, data);
    },
    delete: (id) => {
        return api.delete(`/api/flashcards/${id}`);
    },
    search: (term, page = 0, size = 20) => {
        return api.get('/api/flashcards/search', {
            params: { term, page, size }
        });
    }
};