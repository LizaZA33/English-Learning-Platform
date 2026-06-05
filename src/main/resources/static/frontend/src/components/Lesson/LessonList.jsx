import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import ErrorMessage from '../Common/ErrorMessage';
import './LessonList.css';

const LessonList = () => {
    const [lessons, setLessons] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadLessons();
    }, []);

    const loadLessons = async () => {
        try {
            const response = await lessonService.getMyLessons();
            setLessons(response.data.content || []);
        } catch (err) {
            setError('Ошибка загрузки уроков');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка уроков...</div>;
    }

    return (
        <div className="lesson-list-container">
            <h1>Уроки</h1>

            <ErrorMessage message={error} onClose={() => setError('')} />

            <div className="lessons-grid">
                {lessons.map(lesson => (
                    <Link to={`/lessons/${lesson.id}`} key={lesson.id} className="lesson-card">
                        <h3>{lesson.title}</h3>
                        {lesson.description && <p>{lesson.description}</p>}
                        {lesson.teacher && (
                            <span className="lesson-teacher">
                                {lesson.teacher.lastName} {lesson.teacher.firstName}
                            </span>
                        )}
                        <div className="lesson-footer">
                            <span>{lesson.flashcards?.length || 0} карточек</span>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default LessonList;