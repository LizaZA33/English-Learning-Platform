// src/components/Flashcard/EditFlashcardSet.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import { flashcardService } from '../../services/flashcardService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './CreateFlashcardSet.css';

const EditFlashcardSet = () => {
    const { lessonId } = useParams();
    const navigate = useNavigate();
    const { user, hasRole } = useAuth();
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [flashcards, setFlashcards] = useState([]);
    const [originalFlashcards, setOriginalFlashcards] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [canEdit, setCanEdit] = useState(false);

    useEffect(() => {
        loadLesson();
    }, [lessonId]);

    const loadLesson = async () => {
        try {
            const response = await lessonService.getById(lessonId);
            const lesson = response.data;
            setTitle(lesson.title);
            setDescription(lesson.description || '');
            
            const cards = lesson.flashcards || lesson.flashcardEntities || [];
            setFlashcards(cards.map(card => ({
                id: card.id,
                term: card.term || '',
                definition: card.definition || '',
                example: card.example || '',
                translation: card.translation || '',
                difficulty: card.difficulty || 1
            })));
            setOriginalFlashcards(cards.map(card => card.id));
            
            const isOwner = lesson.owner?.id === user?.id;
            const isTeacher = hasRole('TEACHER') && lesson.teacher?.id === user?.teacher?.id;
            const isAdmin = hasRole('ADMIN');
            
            if (isOwner || isTeacher || isAdmin) {
                setCanEdit(true);
            } else {
                setError('У вас нет прав на редактирование этого набора');
            }
        } catch (err) {
            setError('Ошибка загрузки набора');
        } finally {
            setLoading(false);
        }
    };

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

        setSaving(true);

        try {
            await lessonService.update(lessonId, { title, description });
        
            const existingCardIds = originalFlashcards;
            const newCardIds = flashcards.filter(c => c.id).map(c => c.id);
        
            for (const oldId of existingCardIds) {
                if (!newCardIds.includes(oldId)) {
                    await flashcardService.delete(oldId);
                }
            }
            
            for (const card of flashcards) {
                const cardData = {
                    lessonId: parseInt(lessonId),
                    term: card.term,
                    definition: card.definition || '',
                    example: card.example || '',
                    translation: card.translation,
                    difficulty: card.difficulty || 1
                };
                
                if (card.id) {
                    await flashcardService.update(card.id, cardData);
                } else {
                    await flashcardService.create(cardData);
                }
            }

            navigate(`/flashcards/${lessonId}`);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка сохранения набора');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (!canEdit) {
        return (
            <div className="create-set-container">
                <h1>Доступ запрещен</h1>
                <p>У вас нет прав на редактирование этого набора карточек.</p>
                <button onClick={() => navigate(`/flashcards/${lessonId}`)} className="btn-primary">
                    Вернуться к набору
                </button>
            </div>
        );
    }

    return (
        <div className="create-set-container">
            <h1>Редактирование набора</h1>

            <ErrorMessage message={error} onClose={() => setError('')} />

            <form onSubmit={handleSubmit} className="create-set-form">
                <div className="set-info">
                    <div className="form-group">
                        <label>Название набора *</label>
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className="form-input"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Описание</label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
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
                                    <button type="button" onClick={() => removeCard(index)} className="btn-remove">
                                        Удалить
                                    </button>
                                )}
                            </div>
                            <div className="card-editor-fields">
                                <div className="form-group">
                                    <label>Термин (английский) *</label>
                                    <input
                                        type="text"
                                        value={card.term}
                                        onChange={(e) => updateCard(index, 'term', e.target.value)}
                                        className="form-input"
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Перевод (русский) *</label>
                                    <input
                                        type="text"
                                        value={card.translation}
                                        onChange={(e) => updateCard(index, 'translation', e.target.value)}
                                        className="form-input"
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Определение</label>
                                    <input
                                        type="text"
                                        value={card.definition}
                                        onChange={(e) => updateCard(index, 'definition', e.target.value)}
                                        className="form-input"
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Пример</label>
                                    <input
                                        type="text"
                                        value={card.example}
                                        onChange={(e) => updateCard(index, 'example', e.target.value)}
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

                    <button type="button" onClick={addCard} className="btn-add-card">
                        + Добавить карточку
                    </button>
                </div>

                <div className="form-actions">
                    <button type="button" onClick={() => navigate(`/flashcards/${lessonId}`)} className="btn-cancel">
                        Отмена
                    </button>
                    <button type="submit" className="btn-primary" disabled={saving}>
                        {saving ? 'Сохранение...' : 'Сохранить изменения'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditFlashcardSet;