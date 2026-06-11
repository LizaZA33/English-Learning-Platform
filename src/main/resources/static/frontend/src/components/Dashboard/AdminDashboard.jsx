import React, { useState, useEffect } from 'react';
import { userService } from '../../services/userService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './AdminDashboard.css';

const AdminDashboard = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [search, setSearch] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const { hasRole } = useAuth();

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async (searchTerm = '') => {
        try {
            setLoading(true);
            const response = await userService.getAll(searchTerm);
            setUsers(response.data.content || []);
            setError('');
        } catch (err) {
            console.error('Error loading users:', err);
            if (err.response?.status === 403) {
                setError('Доступ запрещен. Убедитесь, что у вас есть права администратора.');
            } else {
                setError('Ошибка загрузки пользователей');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        loadUsers(search);
    };

    const handleRoleUpdate = async (userId, role, add) => {
        try {
            await userService.updateRoles(userId, role, add);
            setSuccessMessage(`Роль ${add ? 'добавлена' : 'удалена'} успешно`);
            loadUsers(search);
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка обновления роли');
        }
    };

    const handleDeleteUser = async (userId) => {
        if (!window.confirm('Вы уверены, что хотите удалить этого пользователя?')) return;
        
        try {
            await userService.delete(userId);
            setSuccessMessage('Пользователь удалён');
            loadUsers(search);
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (err) {
            setError('Ошибка удаления пользователя');
        }
    };

    if (loading) {
        return <div className="loading">Загрузка панели администратора...</div>;
    }

    return (
        <div className="admin-dashboard">
            <h1>Панель администратора</h1>

            {successMessage && (
                <div className="success-message">{successMessage}</div>
            )}
            <ErrorMessage message={error} onClose={() => setError('')} />

            <form onSubmit={handleSearch} className="admin-search">
                <input
                    type="text"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    placeholder="Поиск по email, имени, фамилии..."
                    className="form-input search-input"
                />
                <button type="submit" className="btn-primary">Поиск</button>
            </form>

            <div className="users-table-container">
                <table className="users-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Email</th>
                            <th>Роли</th>
                            <th>Профиль</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.length === 0 ? (
                            <tr>
                                <td colSpan="5" style={{ textAlign: 'center' }}>Нет пользователей</td>
                            </tr>
                        ) : (
                            users.map(user => (
                                <tr key={user.id}>
                                    <td>{user.id}</td>
                                    <td>{user.email}</td>
                                    <td>
                                        <div className="roles-cell">
                                            {user.roles?.map(role => (
                                                <span key={role} className="role-tag">
                                                    {role}
                                                    {role !== 'ADMIN' && (
                                                        <button
                                                            onClick={() => handleRoleUpdate(user.id, role, false)}
                                                            className="role-remove"
                                                            title="Удалить роль"
                                                        >
                                                            ×
                                                        </button>
                                                    )}
                                                </span>
                                            ))}
                                        </div>
                                    </td>
                                    <td>
                                        {user.teacher && (
                                            <div className="profile-info-cell">
                                                Учитель: {user.teacher.lastName} {user.teacher.firstName}
                                            </div>
                                        )}
                                        {user.student && (
                                            <div className="profile-info-cell">
                                                Студент: {user.student.lastName} {user.student.firstName}
                                            </div>
                                        )}
                                    </td>
                                    <td>
                                        <div className="actions-cell">
                                            {!user.roles?.includes('TEACHER') && (
                                                <button
                                                    onClick={() => handleRoleUpdate(user.id, 'TEACHER', true)}
                                                    className="btn-action btn-add-role"
                                                >
                                                    +Учитель
                                                </button>
                                            )}
                                            {!user.roles?.includes('ADMIN') && (
                                                <button
                                                    onClick={() => handleRoleUpdate(user.id, 'ADMIN', true)}
                                                    className="btn-action btn-add-role"
                                                >
                                                    +Админ
                                                </button>
                                            )}
                                            <button
                                                onClick={() => handleDeleteUser(user.id)}
                                                className="btn-action btn-delete"
                                            >
                                                Удалить
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminDashboard;