import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import ErrorMessage from '../Common/ErrorMessage';
import './FlashcardSet.css';

const FlashcardSet = () => {
    const { lessonId } = useParams();
    const navigate = useNavigate();
    const [lesson, setLesson] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadLesson();
    }, [lessonId]);

    const loadLesson = async () => {
        try {
            const response = await lessonService.getById(lessonId);
            setLesson(response.data);
        } catch (err) {
            setError('Ошибка загрузки набора карточек');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (!lesson) {
        return <div className="loading">Набор не найден</div>;
    }

    return (
        <div className="flashcard-set-container">
            <div className="set-breadcrumb">
                <Link to="/flashcards">Мои наборы</Link>
                <span>/</span>
                <span>{lesson.title}</span>
            </div>

            <div className="set-header">
                <h1>{lesson.title}</h1>
                {lesson.description && <p>{lesson.description}</p>}
            </div>

            <ErrorMessage message={error} />

            <div className="study-modes">
                <Link 
                    to={`/flashcards/${lessonId}/study`}
                    className="btn-primary btn-study"
                >
                    Начать изучение
                </Link>
            </div>

            <div className="cards-grid">
                {lesson.flashcards?.map((card, index) => (
                    <div key={card.id || index} className="card-item">
                        <div className="card-front">
                            <span className="card-term">{card.term}</span>
                            <span className="card-difficulty">
                                {'●'.repeat(card.difficulty)}{'○'.repeat(5 - card.difficulty)}
                            </span>
                        </div>
                        <div className="card-back">
                            <span className="card-translation">{card.translation}</span>
                            {card.definition && <p className="card-definition">{card.definition}</p>}
                            {card.example && <p className="card-example">{card.example}</p>}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default FlashcardSet;