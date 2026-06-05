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
    const { hasRole } = useAuth();

    useEffect(() => {
        loadGroups();
    }, []);

    const loadGroups = async () => {
        try {
            const response = await groupService.getAll({});
            setGroups(response.data.content || []);
        } catch (err) {
            setError('Ошибка загрузки групп');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка групп...</div>;
    }

    return (
        <div className="group-list-container">
            <div className="group-list-header">
                <h1>Учебные группы</h1>
                <div className="group-actions">
                    <Link to="/groups/join" className="btn-primary">
                        Вступить в группу
                    </Link>
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
                    <Link to="/groups/join" className="btn-primary">
                        Вступить в группу
                    </Link>
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
                                <span className="invite-code-label">
                                    Код: {group.inviteCode}
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default GroupList;