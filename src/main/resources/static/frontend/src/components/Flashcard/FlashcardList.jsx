import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './FlashcardList.css';

const FlashcardList = () => {
    const [lessons, setLessons] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { hasRole } = useAuth();

    useEffect(() => {
        loadLessons();
    }, []);

    const loadLessons = async () => {
        try {
            const response = await lessonService.getMyLessons();
            setLessons(response.data.content || []);
        } catch (err) {
            setError('Ошибка загрузки наборов карточек');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка наборов карточек...</div>;
    }

    return (
        <div className="flashcard-list-container">
            <div className="flashcard-list-header">
                <h1>Мои наборы карточек</h1>
                <Link to="/flashcards/create" className="btn-primary">
                    Создать новый набор
                </Link>
            </div>

            <ErrorMessage message={error} onClose={() => setError('')} />

            {lessons.length === 0 ? (
                <div className="empty-state">
                    <p>У вас пока нет наборов карточек</p>
                    <Link to="/flashcards/create" className="btn-primary">
                        Создать первый набор
                    </Link>
                </div>
            ) : (
                <div className="flashcard-grid">
                    {lessons.map(lesson => (
                        <Link 
                            to={`/flashcards/${lesson.id}`} 
                            key={lesson.id}
                            className="flashcard-set-card"
                        >
                            <div className="set-card-header">
                                <h3>{lesson.title}</h3>
                                {lesson.teacher && (
                                    <span className="teacher-label">
                                        {lesson.teacher.lastName} {lesson.teacher.firstName}
                                    </span>
                                )}
                            </div>
                            {lesson.description && (
                                <p className="set-card-description">{lesson.description}</p>
                            )}
                            <div className="set-card-footer">
                                <span className="card-count">
                                    {lesson.flashcards?.length || 0} карточек
                                </span>
                                <span className="study-link">Изучать</span>
                            </div>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
};

export default FlashcardList;