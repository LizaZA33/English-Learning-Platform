import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './LessonView.css';

const LessonView = () => {
    const { id } = useParams();
    const { hasRole } = useAuth();
    const [lesson, setLesson] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadLesson();
    }, [id]);

    const loadLesson = async () => {
        try {
            const response = await lessonService.getById(id);
            setLesson(response.data);
        } catch (err) {
            setError('Ошибка загрузки урока');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка урока...</div>;
    }

    if (!lesson) {
        return <div className="loading">Урок не найден</div>;
    }

    const isOwner = hasRole('USER') && lesson.owner?.id === lesson.teacher?.id;

    return (
        <div className="lesson-view-container">
            <div className="lesson-breadcrumb">
                <Link to="/lessons">Уроки</Link>
                <span>/</span>
                <span>{lesson.title}</span>
            </div>

            <ErrorMessage message={error} />

            <div className="lesson-header">
                <h1>{lesson.title}</h1>
                {lesson.description && <p>{lesson.description}</p>}
                {lesson.teacher && (
                    <div className="lesson-teacher-info">
                        Преподаватель: {lesson.teacher.lastName} {lesson.teacher.firstName}
                    </div>
                )}
            </div>

            <div className="lesson-flashcards">
                <h2>Карточки урока</h2>
                <Link 
                    to={`/flashcards/${lesson.id}/study`}
                    className="btn-primary btn-study"
                >
                    Начать изучение
                </Link>
                
                <div className="flashcards-list">
                    {lesson.flashcards?.map(card => (
                        <div key={card.id} className="lesson-flashcard-item">
                            <div className="flashcard-term">{card.term}</div>
                            <div className="flashcard-translation">{card.translation}</div>
                            {card.example && (
                                <div className="flashcard-example">{card.example}</div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default LessonView;