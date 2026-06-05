import api from './api';

export const groupService = {
    getAll: (params, page = 0, size = 10) => {
        return api.get('/api/groups', { params: { ...params, page, size } });
    },
    getById: (id) => {
        return api.get(`/api/groups/${id}`);
    },
    create: (data) => {
        return api.post('/api/groups', data);
    },
    join: (inviteCode) => {
        return api.post('/api/groups/join', null, {
            params: { inviteCode }
        });
    },
    delete: (id) => {
        return api.delete(`/api/groups/${id}`);
    }
};