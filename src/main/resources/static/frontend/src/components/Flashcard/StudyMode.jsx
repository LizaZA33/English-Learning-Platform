import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import './StudyMode.css';

const StudyMode = () => {
    const { lessonId } = useParams();
    const [lesson, setLesson] = useState(null);
    const [cards, setCards] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [flipped, setFlipped] = useState(false);
    const [mode, setMode] = useState('selection');
    const [direction, setDirection] = useState('ru-en');
    const [completed, setCompleted] = useState(false);
    const [results, setResults] = useState({ correct: 0, incorrect: 0 });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadLesson();
    }, [lessonId]);

    const loadLesson = async () => {
        try {
            const response = await lessonService.getById(lessonId);
            const lessonData = response.data;
            setLesson(lessonData);
            if (lessonData.flashcards) {
                const shuffled = [...lessonData.flashcards].sort(() => Math.random() - 0.5);
                setCards(shuffled);
            }
        } catch (err) {
            console.error('Ошибка загрузки:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleFlip = () => {
        setFlipped(!flipped);
    };

    const handleResult = (correct) => {
        setResults(prev => ({
            correct: prev.correct + (correct ? 1 : 0),
            incorrect: prev.incorrect + (correct ? 0 : 1)
        }));

        if (currentIndex < cards.length - 1) {
            setTimeout(() => {
                setFlipped(false);
                setCurrentIndex(prev => prev + 1);
            }, 300);
        } else {
            setCompleted(true);
        }
    };

    const handleRestart = () => {
        const shuffled = [...cards].sort(() => Math.random() - 0.5);
        setCards(shuffled);
        setCurrentIndex(0);
        setFlipped(false);
        setCompleted(false);
        setResults({ correct: 0, incorrect: 0 });
    };

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (mode === 'selection') {
        return (
            <div className="study-mode-container">
                <div className="mode-selection">
                    <h1>Выберите режим изучения</h1>
                    <p className="set-title">{lesson?.title}</p>
                    
                    <div className="mode-cards">
                        <button 
                            className="mode-card"
                            onClick={() => { setMode('flashcard'); setDirection('ru-en'); }}
                        >
                            <h3>Устный перевод</h3>
                            <p>Русский - Английский</p>
                            <span className="mode-icon">RU - EN</span>
                        </button>
                        
                        <button 
                            className="mode-card"
                            onClick={() => { setMode('flashcard'); setDirection('en-ru'); }}
                        >
                            <h3>Устный перевод</h3>
                            <p>Английский - Русский</p>
                            <span className="mode-icon">EN - RU</span>
                        </button>
                        
                        <button 
                            className="mode-card"
                            onClick={() => { setMode('written'); setDirection('ru-en'); }}
                        >
                            <h3>Письменный перевод</h3>
                            <p>Русский - Английский</p>
                            <span className="mode-icon">RU - EN</span>
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    if (completed) {
        const total = results.correct + results.incorrect;
        const percentage = Math.round((results.correct / total) * 100);

        return (
            <div className="study-mode-container">
                <div className="results-card">
                    <h2>Изучение завершено</h2>
                    <div className="results-circle">
                        <span className="results-percentage">{percentage}%</span>
                    </div>
                    <div className="results-details">
                        <div className="result-item correct">
                            <span>Правильно</span>
                            <strong>{results.correct}</strong>
                        </div>
                        <div className="result-item incorrect">
                            <span>Неправильно</span>
                            <strong>{results.incorrect}</strong>
                        </div>
                    </div>
                    <div className="results-actions">
                        <button onClick={handleRestart} className="btn-primary">
                            Повторить
                        </button>
                        <Link to={`/flashcards/${lessonId}`} className="btn-secondary-link">
                            К набору
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    const currentCard = cards[currentIndex];

    return (
        <div className="study-mode-container">
            <div className="study-header">
                <span className="progress-counter">
                    {currentIndex + 1} / {cards.length}
                </span>
                <div className="progress-bar">
                    <div 
                        className="progress-fill"
                        style={{ width: `${((currentIndex) / cards.length) * 100}%` }}
                    />
                </div>
            </div>

            <div 
                className={`flashcard-study ${flipped ? 'flipped' : ''}`}
                onClick={handleFlip}
            >
                <div className="flashcard-inner">
                    <div className="flashcard-front">
                        <span className="card-label">
                            {direction === 'ru-en' ? 'Русский' : 'Английский'}
                        </span>
                        <span className="card-word">
                            {direction === 'ru-en' ? currentCard.translation : currentCard.term}
                        </span>
                        {!flipped && (
                            <span className="flip-hint">Нажмите, чтобы увидеть перевод</span>
                        )}
                    </div>
                    <div className="flashcard-back">
                        <span className="card-label">
                            {direction === 'ru-en' ? 'Английский' : 'Русский'}
                        </span>
                        <span className="card-word">
                            {direction === 'ru-en' ? currentCard.term : currentCard.translation}
                        </span>
                        {currentCard.example && (
                            <p className="card-example">{currentCard.example}</p>
                        )}
                    </div>
                </div>
            </div>

            {flipped && (
                <div className="answer-buttons">
                    <button 
                        className="btn-incorrect"
                        onClick={() => handleResult(false)}
                    >
                        Не помню
                    </button>
                    <button 
                        className="btn-correct"
                        onClick={() => handleResult(true)}
                    >
                        Знаю
                    </button>
                </div>
            )}
        </div>
    );
};

export default StudyMode;