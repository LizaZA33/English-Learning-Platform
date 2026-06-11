import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { lectureService } from '../../services/lectureService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './LectureList.css';

const LectureList = () => {
    const [lectures, setLectures] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [search, setSearch] = useState('');
    const { hasRole } = useAuth();

    useEffect(() => {
        loadLectures();
    }, []);

    const loadLectures = async () => {
        try {
            const response = await lectureService.getAll();
            setLectures(response.data.content || []);
        } catch (err) {
            console.error('Error loading lectures:', err);
            if (err.response?.status === 403) {
                setError('У вас нет доступа к лекциям. Возможно, вы не состоите в группе.');
            } else {
                setError('Ошибка загрузки лекций');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!search.trim()) {
            loadLectures();
            return;
        }
        try {
            const response = await lectureService.search(search);
            setLectures(response.data.content || []);
        } catch (err) {
            setError('Ошибка поиска');
        }
    };

    if (loading) {
        return <div className="loading">Загрузка лекций...</div>;
    }

    return (
        <div className="lecture-list-container">
            <h1>Лекции</h1>

            <form onSubmit={handleSearch} className="search-form">
                <input
                    type="text"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    placeholder="Поиск лекций..."
                    className="form-input search-input"
                />
                <button type="submit" className="btn-primary">Поиск</button>
            </form>

            <ErrorMessage message={error} onClose={() => setError('')} />

            {lectures.length === 0 ? (
                <div className="empty-state">
                    <p>Лекции пока не добавлены</p>
                </div>
            ) : (
                <div className="lectures-grid">
                    {lectures.map(lecture => (
                        <Link to={`/lectures/${lecture.id}`} key={lecture.id} className="lecture-card">
                            <h3>{lecture.title}</h3>
                            {lecture.module && (
                                <span className="lecture-module">{lecture.module.name}</span>
                            )}
                            <p className="lecture-preview">
                                {lecture.content?.substring(0, 150)}...
                            </p>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
};

export default LectureList;