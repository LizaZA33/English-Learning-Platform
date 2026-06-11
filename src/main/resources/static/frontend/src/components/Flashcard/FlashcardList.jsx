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
    const [deletingId, setDeletingId] = useState(null);
    const { hasRole } = useAuth();

    useEffect(() => {
        loadLessons();
    }, []);

    const loadLessons = async () => {
        try {
            setLoading(true);
            const response = await lessonService.getMyLessons();
            setLessons(response.data.content || []);
        } catch (err) {
            console.error('Load error:', err);
            setError(err.response?.data?.message || 'Ошибка загрузки наборов карточек');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (lesson) => {
        const confirmed = window.confirm(
            `Вы уверены, что хотите удалить набор "${lesson.title}"?\n\nЭто действие невозможно отменить. Все карточки в этом наборе будут удалены.`
        );
        
        if (!confirmed) return;
        
        setDeletingId(lesson.id);
        try {
            await lessonService.delete(lesson.id);
            setLessons(prev => prev.filter(l => l.id !== lesson.id));
        } catch (err) {
            console.error('Delete error:', err);
            let errorMessage = 'Ошибка при удалении набора';
            if (err.response?.data?.message) {
                errorMessage = err.response.data.message;
            } else if (err.response?.status === 403) {
                errorMessage = 'У вас нет прав на удаление этого набора';
            }
            setError(errorMessage);
        } finally {
            setDeletingId(null);
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
                        <div key={lesson.id} className="flashcard-set-card">
                            <Link 
                                to={`/flashcards/${lesson.id}`} 
                                className="set-card-link"
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
                                    <span className="study-link">Изучать →</span>
                                </div>
                            </Link>
                            <button
                                className="btn-delete-set"
                                onClick={(e) => {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    handleDelete(lesson);
                                }}
                                disabled={deletingId === lesson.id}
                            >
                                {deletingId === lesson.id ? 'Удаление...' : 'Удалить'}
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default FlashcardList;