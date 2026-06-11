import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { groupService } from '../../services/groupService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './GroupList.css';

const GroupList = () => {
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { hasRole, user } = useAuth();

    useEffect(() => {
        loadGroups();
    }, []);

    const loadGroups = async () => {
        try {
            // Пытаемся получить группы текущего пользователя
            const response = await groupService.getMyGroups();
            setGroups(response.data.content || []);
        } catch (err) {
            console.error('Error loading groups:', err);
            if (err.response?.status === 403) {
                setError('У вас нет доступа к группам. Возможно, вы не состоите ни в одной группе.');
            } else {
                setError('Ошибка загрузки групп');
            }
        } finally {
            setLoading(false);
        }
    };

    const copyInviteCode = (code) => {
        navigator.clipboard.writeText(code);
        alert('Код приглашения скопирован: ' + code);
    };

    if (loading) {
        return <div className="loading">Загрузка групп...</div>;
    }

    return (
        <div className="group-list-container">
            <div className="group-list-header">
                <h1>Мои группы</h1>
                <div className="group-actions">
                    {!hasRole('STUDENT') && !hasRole('TEACHER') && (
                        <Link to="/groups/join" className="btn-primary">
                            Вступить в группу
                        </Link>
                    )}
                    {hasRole('TEACHER') && (
                        <Link to="/groups/create" className="btn-secondary">
                            Создать группу
                        </Link>
                    )}
                </div>
            </div>

            <ErrorMessage message={error} onClose={() => setError('')} />

            {groups.length === 0 ? (
                <div className="empty-state">
                    <p>Вы пока не состоите ни в одной группе</p>
                    {!hasRole('STUDENT') && !hasRole('TEACHER') && (
                        <Link to="/groups/join" className="btn-primary">
                            Вступить в группу
                        </Link>
                    )}
                    {hasRole('TEACHER') && (
                        <Link to="/groups/create" className="btn-primary">
                            Создать первую группу
                        </Link>
                    )}
                </div>
            ) : (
                <div className="groups-grid">
                    {groups.map(group => (
                        <div key={group.id} className="group-card">
                            <h3>{group.name}</h3>
                            {group.module && (
                                <span className="group-module">{group.module.name}</span>
                            )}
                            {group.teacher && (
                                <p className="group-teacher">
                                    Преподаватель: {group.teacher.lastName} {group.teacher.firstName}
                                </p>
                            )}
                            <div className="group-footer">
                                <span className="student-count">
                                    Студентов: {group.studentCount || 0}
                                </span>
                                {hasRole('TEACHER') && group.inviteCode && (
                                    <button 
                                        className="copy-code-btn"
                                        onClick={() => copyInviteCode(group.inviteCode)}
                                    >
                                        Код: {group.inviteCode} 📋
                                    </button>
                                )}
                                {hasRole('STUDENT') && group.inviteCode && (
                                    <span className="invite-code-label">
                                        Код группы: {group.inviteCode}
                                    </span>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default GroupList;