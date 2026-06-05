import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { groupService } from '../../services/groupService';
import ErrorMessage from '../Common/ErrorMessage';
import './JoinGroup.css';

const JoinGroup = () => {
    const [inviteCode, setInviteCode] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!inviteCode.trim()) {
            setError('Введите код приглашения');
            return;
        }

        setLoading(true);

        try {
            const response = await groupService.join(inviteCode.trim());
            setSuccess('Вы успешно вступили в группу! Теперь у вас есть доступ к лекциям и урокам.');
            setTimeout(() => {
                navigate('/groups');
            }, 2000);
        } catch (err) {
            if (err.response?.status === 404) {
                setError('Группа с таким кодом не найдена. Проверьте правильность кода.');
            } else if (err.response?.status === 400) {
                setError('Вы уже состоите в этой группе');
            } else {
                setError('Ошибка при вступлении в группу');
            }
        } finally {
            setLoading(false);
        }
    };

    const handlePaste = async () => {
        try {
            const text = await navigator.clipboard.readText();
            setInviteCode(text.trim());
        } catch (err) {
            console.log('Браузер не поддерживает чтение из буфера обмена');
        }
    };

    return (
        <div className="join-group-container">
            <div className="join-group-card">
                <h1>Вступить в группу</h1>
                <p className="join-description">
                    Введите код приглашения, полученный от учителя, чтобы присоединиться к учебной группе.
                    После вступления вы получите доступ к лекциям, урокам и наборам карточек от преподавателя.
                </p>

                <ErrorMessage message={error} onClose={() => setError('')} />
                
                {success && (
                    <div className="success-message">
                        {success}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="inviteCode">Код приглашения</label>
                        <div className="code-input-wrapper">
                            <input
                                id="inviteCode"
                                type="text"
                                value={inviteCode}
                                onChange={(e) => setInviteCode(e.target.value)}
                                placeholder="XXXXXXXXXXXX"
                                className="form-input code-input"
                                maxLength={12}
                                autoComplete="off"
                            />
                            <button 
                                type="button" 
                                className="paste-btn"
                                onClick={handlePaste}
                                title="Вставить из буфера"
                            >
                                Вставить
                            </button>
                        </div>
                        <p className="code-hint">Код состоит из 12 символов (буквы и цифры)</p>
                    </div>

                    <button 
                        type="submit" 
                        className="btn-primary btn-full"
                        disabled={loading || !inviteCode.trim()}
                    >
                        {loading ? 'Вступление...' : 'Вступить в группу'}
                    </button>
                </form>

                <div className="join-info">
                    <h3>Как получить код?</h3>
                    <p>Попросите код у вашего преподавателя. Код приглашения отображается в карточке группы у учителя.</p>
                </div>
            </div>
        </div>
    );
};

export default JoinGroup;