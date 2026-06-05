import api from './api';

export const progressService = {
    getLectureProgress: (page = 0, size = 10) => {
        return api.get('/api/progress/lectures', { params: { page, size } });
    },
    getLessonProgress: (page = 0, size = 10) => {
        return api.get('/api/progress/lessons', { params: { page, size } });
    },
    updateLectureProgress: (lectureId, progressPercent) => {
        return api.put(`/api/progress/lectures/${lectureId}`, null, {
            params: { progressPercent }
        });
    },
    updateLessonProgress: (lessonId, progressPercent) => {
        return api.put(`/api/progress/lessons/${lessonId}`, null, {
            params: { progressPercent }
        });
    }
};