import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { lessonService } from '../../services/lessonService';
import { flashcardService } from '../../services/flashcardService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './FlashcardSet.css';

const FlashcardSet = () => {
    const { lessonId } = useParams();
    const navigate = useNavigate();
    const [lesson, setLesson] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedDescription, setEditedDescription] = useState('');
    const [flashcards, setFlashcards] = useState([]);
    const [editingCard, setEditingCard] = useState(null);
    const { user, hasRole } = useAuth();

    const canEdit = () => {
        if (hasRole('ADMIN')) return true;
        if (lesson?.owner?.id === user?.id) return true;
        if (lesson?.teacher?.id === user?.teacher?.id) return true;
        return false;
    };

    useEffect(() => {
        loadLesson();
    }, [lessonId]);

    const loadLesson = async () => {
        try {
            const response = await lessonService.getById(lessonId);
            const lessonData = response.data;
            setLesson(lessonData);
            setEditedTitle(lessonData.title);
            setEditedDescription(lessonData.description || '');
            const cards = lessonData.flashcards || lessonData.flashcardEntities || [];
            setFlashcards(cards);
        } catch (err) {
            setError('Ошибка загрузки набора карточек');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSaveLesson = async () => {
        try {
            await lessonService.update(lessonId, {
                title: editedTitle,
                description: editedDescription
            });
            setIsEditing(false);
            loadLesson();
        } catch (err) {
            setError('Ошибка сохранения');
        }
    };

    const handleDeleteCard = async (cardId) => {
        if (!window.confirm('Удалить эту карточку?')) return;
        try {
            await flashcardService.delete(cardId);
            loadLesson();
        } catch (err) {
            setError('Ошибка удаления карточки');
        }
    };

    const handleSaveCard = async (cardData) => {
        try {
            if (cardData.id) {
                await flashcardService.update(cardData.id, cardData);
            } else {
                await flashcardService.create({
                    lessonId: parseInt(lessonId),
                    ...cardData
                });
            }
            setEditingCard(null);
            loadLesson();
        } catch (err) {
            setError('Ошибка сохранения карточки');
        }
    };

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (!lesson) {
        return <div className="loading">Набор не найден</div>;
    }

    const flashcardsList = flashcards;

    return (
        <div className="flashcard-set-container">
            <div className="set-breadcrumb">
                <Link to="/flashcards">Мои наборы</Link>
                <span>/</span>
                {isEditing ? (
                    <span>Редактирование</span>
                ) : (
                    <span>{lesson.title}</span>
                )}
            </div>

            {isEditing ? (
                <div className="edit-header">
                    <input
                        type="text"
                        value={editedTitle}
                        onChange={(e) => setEditedTitle(e.target.value)}
                        className="form-input edit-title"
                        placeholder="Название набора"
                    />
                    <textarea
                        value={editedDescription}
                        onChange={(e) => setEditedDescription(e.target.value)}
                        className="form-input edit-description"
                        rows="2"
                        placeholder="Описание набора"
                    />
                    <div className="edit-actions">
                        <button onClick={() => setIsEditing(false)} className="btn-cancel">
                            Отмена
                        </button>
                        <button onClick={handleSaveLesson} className="btn-primary">
                            Сохранить изменения
                        </button>
                    </div>
                </div>
            ) : (
                <div className="set-header">
                    <h1>{lesson.title}</h1>
                    {lesson.description && <p>{lesson.description}</p>}
                    {canEdit() && (
                        <button onClick={() => setIsEditing(true)} className="btn-edit-set">
                            Редактировать набор
                        </button>
                    )}
                </div>
            )}

            <ErrorMessage message={error} />

            <div className="study-modes">
                {flashcardsList.length > 0 ? (
                    <Link 
                        to={`/flashcards/${lessonId}/study`}
                        className="btn-primary btn-study"
                    >
                        Начать изучение ({flashcardsList.length} карточек)
                    </Link>
                ) : (
                    <div className="no-cards-message">
                        <p>В этом наборе пока нет карточек</p>
                    </div>
                )}
                
                {canEdit() && (
                    <button 
                        onClick={() => setEditingCard({ term: '', translation: '', definition: '', example: '', difficulty: 1 })}
                        className="btn-secondary"
                    >
                        Добавить карточку
                    </button>
                )}
            </div>

            <div className="cards-grid">
                {flashcardsList.map((card, index) => (
                    <div key={card.id || index} className="card-item">
                        {editingCard?.id === card.id ? (
                            <CardEditor
                                card={editingCard}
                                onSave={handleSaveCard}
                                onCancel={() => setEditingCard(null)}
                            />
                        ) : (
                            <>
                                <div className="card-front">
                                    <span className="card-term">{card.term}</span>
                                    <span className="card-difficulty">
                                        {'●'.repeat(card.difficulty || 1)}{'○'.repeat(5 - (card.difficulty || 1))}
                                    </span>
                                </div>
                                <div className="card-back">
                                    <span className="card-translation">{card.translation}</span>
                                    {card.definition && <p className="card-definition">{card.definition}</p>}
                                    {card.example && <p className="card-example">{card.example}</p>}
                                </div>
                                {canEdit() && (
                                    <div className="card-actions">
                                        <button onClick={() => setEditingCard(card)} className="btn-edit-card">
                                            Редактировать
                                        </button>
                                        <button onClick={() => handleDeleteCard(card.id)} className="btn-delete-card">
                                            Удалить
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                ))}
            </div>

            {editingCard && !editingCard.id && (
                <div className="modal-overlay" onClick={() => setEditingCard(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h3>Добавление карточки</h3>
                        <CardEditor
                            card={editingCard}
                            onSave={handleSaveCard}
                            onCancel={() => setEditingCard(null)}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};

// Компонент редактирования карточки
const CardEditor = ({ card, onSave, onCancel }) => {
    const [formData, setFormData] = useState({
        term: card.term || '',
        translation: card.translation || '',
        definition: card.definition || '',
        example: card.example || '',
        difficulty: card.difficulty || 1
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave({ id: card.id, ...formData });
    };

    return (
        <form onSubmit={handleSubmit} className="card-editor-form">
            <div className="form-group">
                <label>Термин (английский) *</label>
                <input
                    type="text"
                    value={formData.term}
                    onChange={(e) => setFormData({ ...formData, term: e.target.value })}
                    required
                    className="form-input"
                    placeholder="Например: apple"
                />
            </div>
            <div className="form-group">
                <label>Перевод (русский) *</label>
                <input
                    type="text"
                    value={formData.translation}
                    onChange={(e) => setFormData({ ...formData, translation: e.target.value })}
                    required
                    className="form-input"
                    placeholder="Например: яблоко"
                />
            </div>
            <div className="form-group">
                <label>Определение (необязательно)</label>
                <input
                    type="text"
                    value={formData.definition}
                    onChange={(e) => setFormData({ ...formData, definition: e.target.value })}
                    className="form-input"
                    placeholder="Например: A round fruit with red or green skin"
                />
            </div>
            <div className="form-group">
                <label>Пример использования (необязательно)</label>
                <input
                    type="text"
                    value={formData.example}
                    onChange={(e) => setFormData({ ...formData, example: e.target.value })}
                    className="form-input"
                    placeholder="Например: I ate an apple for breakfast"
                />
            </div>
            <div className="form-group">
                <label>Сложность (1-5)</label>
                <select
                    value={formData.difficulty}
                    onChange={(e) => setFormData({ ...formData, difficulty: parseInt(e.target.value) })}
                    className="form-input"
                >
                    <option value="1">1 - Очень легко</option>
                    <option value="2">2 - Легко</option>
                    <option value="3">3 - Средне</option>
                    <option value="4">4 - Сложно</option>
                    <option value="5">5 - Очень сложно</option>
                </select>
            </div>
            <div className="form-actions">
                <button type="button" onClick={onCancel} className="btn-cancel">
                    Отмена
                </button>
                <button type="submit" className="btn-primary">
                    Сохранить карточку
                </button>
            </div>
        </form>
    );
};

export default FlashcardSet;