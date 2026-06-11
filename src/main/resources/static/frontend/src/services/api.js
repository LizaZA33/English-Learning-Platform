import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    timeout: 10000,
    withCredentials: false
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url} - Authenticated`);
        } else {
            console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url} - No token`);
        }
        return config;
    },
    (error) => {
        console.error('[API Request Error]', error);
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
        console.log(`[API Response] ${response.status} ${response.config.url}`);
        return response;
    },
    (error) => {
        const status = error.response?.status;
        const url = error.response?.config?.url;
        const message = error.response?.data?.message || error.message;
        
        console.error(`[API Error] ${status} ${url} - ${message}`);

        if (status === 401) {
            console.warn('[API Error] Token expired or invalid, redirecting to login');
            localStorage.removeItem('token');
            // Проверяем, что мы не на странице логина уже
            if (!window.location.pathname.includes('/login')) {
                window.location.href = '/login';
            }
        }
        
        if (status === 403) {
            console.warn('[API Error] Access denied for:', url);
        }
        
        return Promise.reject(error);
    }
);

export default api;