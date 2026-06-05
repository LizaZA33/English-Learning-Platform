import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import { flashcardService } from '../../services/flashcardService';
import ErrorMessage from '../Common/ErrorMessage';
import './CreateFlashcardSet.css';

const CreateFlashcardSet = () => {
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [flashcards, setFlashcards] = useState([
        { term: '', definition: '', example: '', translation: '', difficulty: 1 }
    ]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const addCard = () => {
        setFlashcards([...flashcards, { term: '', definition: '', example: '', translation: '', difficulty: 1 }]);
    };

    const removeCard = (index) => {
        if (flashcards.length > 1) {
            setFlashcards(flashcards.filter((_, i) => i !== index));
        }
    };

    const updateCard = (index, field, value) => {
        const updated = flashcards.map((card, i) => 
            i === index ? { ...card, [field]: value } : card
        );
        setFlashcards(updated);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!title.trim()) {
            setError('Введите название набора');
            return;
        }

        const invalidCards = flashcards.filter(card => !card.term.trim() || !card.translation.trim());
        if (invalidCards.length > 0) {
            setError('Все карточки должны содержать термин и перевод');
            return;
        }

        setLoading(true);

        try {
            const lessonResponse = await lessonService.create({
                title,
                description
            });

            const lessonId = lessonResponse.data.id;

            for (const card of flashcards) {
                await flashcardService.create({
                    lessonId,
                    term: card.term,
                    definition: card.definition,
                    example: card.example,
                    translation: card.translation,
                    difficulty: card.difficulty
                });
            }

            navigate(`/flashcards/${lessonId}`);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка создания набора карточек');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="create-set-container">
            <h1>Создание набора карточек</h1>

            <ErrorMessage message={error} onClose={() => setError('')} />

            <form onSubmit={handleSubmit} className="create-set-form">
                <div className="set-info">
                    <div className="form-group">
                        <label>Название набора</label>
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="Например: Неправильные глаголы"
                            className="form-input"
                        />
                    </div>
                    <div className="form-group">
                        <label>Описание (необязательно)</label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            placeholder="Краткое описание набора"
                            className="form-input form-textarea"
                            rows="3"
                        />
                    </div>
                </div>

                <div className="cards-section">
                    <h2>Карточки</h2>
                    
                    {flashcards.map((card, index) => (
                        <div key={index} className="card-editor">
                            <div className="card-editor-header">
                                <span>Карточка {index + 1}</span>
                                {flashcards.length > 1 && (
                                    <button 
                                        type="button" 
                                        onClick={() => removeCard(index)}
                                        className="btn-remove"
                                    >
                                        Удалить
                                    </button>
                                )}
                            </div>
                            <div className="card-editor-fields">
                                <div className="form-group">
                                    <label>Термин (английский)</label>
                                    <input
                                        type="text"
                                        value={card.term}
                                        onChange={(e) => updateCard(index, 'term', e.target.value)}
                                        placeholder="apple"
                                        className="form-input"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Перевод (русский)</label>
                                    <input
                                        type="text"
                                        value={card.translation}
                                        onChange={(e) => updateCard(index, 'translation', e.target.value)}
                                        placeholder="яблоко"
                                        className="form-input"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Определение (необязательно)</label>
                                    <input
                                        type="text"
                                        value={card.definition}
                                        onChange={(e) => updateCard(index, 'definition', e.target.value)}
                                        placeholder="A round fruit with red or green skin"
                                        className="form-input"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Пример использования</label>
                                    <input
                                        type="text"
                                        value={card.example}
                                        onChange={(e) => updateCard(index, 'example', e.target.value)}
                                        placeholder="I ate an apple for breakfast"
                                        className="form-input"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Сложность (1-5)</label>
                                    <select
                                        value={card.difficulty}
                                        onChange={(e) => updateCard(index, 'difficulty', parseInt(e.target.value))}
                                        className="form-input"
                                    >
                                        <option value="1">1 - Очень легко</option>
                                        <option value="2">2 - Легко</option>
                                        <option value="3">3 - Средне</option>
                                        <option value="4">4 - Сложно</option>
                                        <option value="5">5 - Очень сложно</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    ))}

                    <button 
                        type="button" 
                        onClick={addCard}
                        className="btn-add-card"
                    >
                        + Добавить карточку
                    </button>
                </div>

                <div className="form-actions">
                    <button 
                        type="button" 
                        onClick={() => navigate('/flashcards')}
                        className="btn-cancel"
                    >
                        Отмена
                    </button>
                    <button 
                        type="submit" 
                        className="btn-primary"
                        disabled={loading}
                    >
                        {loading ? 'Создание...' : 'Создать набор'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateFlashcardSet;