import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { userService } from '../../services/userService';
import ErrorMessage from '../Common/ErrorMessage';
import './Profile.css';

const Profile = () => {
    const { user, hasRole, refreshUser } = useAuth();
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        patronymic: '',
        phoneNumber: '',
        dateOfBirth: ''
    });

    // Обновляем форму при изменении user
    useEffect(() => {
        if (user) {
            const profile = user.student || user.teacher || {};
            setFormData({
                firstName: profile.firstName || '',
                lastName: profile.lastName || '',
                patronymic: profile.patronymic || '',
                phoneNumber: profile.phoneNumber || '',
                dateOfBirth: profile.dateOfBirth || ''
            });
        }
    }, [user]);

    const isAdmin = hasRole('ADMIN');
    const isTeacher = hasRole('TEACHER');
    const isStudent = hasRole('STUDENT');
    const hasProfile = isTeacher || isStudent;
    
    // Для обычного пользователя (USER) - показываем форму создания профиля
    const isSimpleUser = !isAdmin && !isTeacher && !isStudent;

    const handleCreateStudentProfile = async () => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            await userService.createStudentProfile(formData);
            setSuccess('Профиль студента успешно создан!');
            await refreshUser();
            setTimeout(() => setSuccess(''), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка создания профиля студента');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateTeacherProfile = async () => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            await userService.createTeacherProfile(formData);
            setSuccess('Профиль учителя успешно создан!');
            await refreshUser();
            setTimeout(() => setSuccess(''), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка создания профиля учителя');
        } finally {
            setLoading(false);
        }
    };

    const handleSaveProfile = async () => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            const updateData = {
                firstName: formData.firstName || null,
                lastName: formData.lastName || null,
                patronymic: formData.patronymic || null,
                phoneNumber: formData.phoneNumber || null,
                dateOfBirth: formData.dateOfBirth || null
            };
            
            await userService.updateProfile(updateData);
            setSuccess('Профиль успешно обновлен');
            await refreshUser();
            setIsEditing(false);
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка сохранения профиля');
        } finally {
            setLoading(false);
        }
    };

    const getFullName = () => {
        if (hasProfile) {
            const parts = [];
            if (formData.lastName) parts.push(formData.lastName);
            if (formData.firstName) parts.push(formData.firstName);
            if (formData.patronymic) parts.push(formData.patronymic);
            return parts.join(' ') || 'Не указано';
        }
        return user?.email?.split('@')[0] || 'Пользователь';
    };

    const getRoleDisplay = (role) => {
        const roleMap = {
            'USER': 'Пользователь',
            'STUDENT': 'Студент',
            'TEACHER': 'Учитель',
            'ADMIN': 'Администратор'
        };
        return roleMap[role] || role;
    };

    if (!user) {
        return <div className="loading">Загрузка профиля...</div>;
    }

    // Для администратора
    if (isAdmin) {
        return (
            <div className="profile-container">
                <h1 className="profile-title">Профиль администратора</h1>
                <div className="profile-card">
                    <div className="profile-header">
                        <div className="profile-avatar">
                            {user.email.charAt(0).toUpperCase()}
                        </div>
                        <div className="profile-info">
                            <h2>Администратор</h2>
                            <p className="profile-email">{user.email}</p>
                            <div className="profile-roles">
                                {user.roles?.map(role => (
                                    <span key={role} className="role-badge">
                                        {getRoleDisplay(role)}
                                    </span>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className="profile-details">
                        <h3>Информация</h3>
                        <div className="details-grid">
                            <div className="detail-item">
                                <span className="detail-label">Email</span>
                                <span className="detail-value">{user.email}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Роли</span>
                                <span className="detail-value">{user.roles?.join(', ')}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-container">
            <h1 className="profile-title">Профиль пользователя</h1>
            
            <div className="profile-card">
                <div className="profile-header">
                    <div className="profile-avatar">
                        {getFullName().charAt(0).toUpperCase() || user.email.charAt(0).toUpperCase()}
                    </div>
                    <div className="profile-info">
                        <h2>{getFullName()}</h2>
                        <p className="profile-email">{user.email}</p>
                        <div className="profile-roles">
                            {user.roles?.map(role => (
                                <span key={role} className="role-badge">
                                    {getRoleDisplay(role)}
                                </span>
                            ))}
                        </div>
                    </div>
                    {hasProfile && !isEditing && (
                        <button className="btn-edit" onClick={() => setIsEditing(true)}>
                            Редактировать профиль
                        </button>
                    )}
                </div>

                <ErrorMessage message={error} onClose={() => setError('')} />
                {success && <div className="success-message">{success}</div>}

                {/* Режим редактирования для STUDENT/TEACHER */}
                {isEditing && hasProfile ? (
                    <div className="profile-details">
                        <h3>Редактирование данных</h3>
                        <div className="form-grid">
                            <div className="form-group">
                                <label>Фамилия</label>
                                <input
                                    type="text"
                                    value={formData.lastName}
                                    onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                                    className="form-input"
                                    placeholder="Введите фамилию"
                                />
                            </div>
                            <div className="form-group">
                                <label>Имя</label>
                                <input
                                    type="text"
                                    value={formData.firstName}
                                    onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                                    className="form-input"
                                    placeholder="Введите имя"
                                />
                            </div>
                            <div className="form-group">
                                <label>Отчество</label>
                                <input
                                    type="text"
                                    value={formData.patronymic}
                                    onChange={(e) => setFormData({...formData, patronymic: e.target.value})}
                                    className="form-input"
                                    placeholder="Введите отчество"
                                />
                            </div>
                            <div className="form-group">
                                <label>Телефон</label>
                                <input
                                    type="tel"
                                    value={formData.phoneNumber}
                                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                                    className="form-input"
                                    placeholder="+79991234567"
                                />
                            </div>
                            {isStudent && (
                                <div className="form-group">
                                    <label>Дата рождения</label>
                                    <input
                                        type="date"
                                        value={formData.dateOfBirth}
                                        onChange={(e) => setFormData({...formData, dateOfBirth: e.target.value})}
                                        className="form-input"
                                    />
                                </div>
                            )}
                        </div>
                        <div className="profile-actions">
                            <button className="btn-cancel" onClick={() => setIsEditing(false)}>Отмена</button>
                            <button onClick={handleSaveProfile} className="btn-primary" disabled={loading}>
                                {loading ? 'Сохранение...' : 'Сохранить изменения'}
                            </button>
                        </div>
                    </div>
                ) : hasProfile ? (
                    // Просмотр профиля для STUDENT/TEACHER
                    <div className="profile-details">
                        <h3>Личные данные</h3>
                        <div className="details-grid">
                            <div className="detail-item">
                                <span className="detail-label">Фамилия</span>
                                <span className="detail-value">{formData.lastName || '—'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Имя</span>
                                <span className="detail-value">{formData.firstName || '—'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Отчество</span>
                                <span className="detail-value">{formData.patronymic || '—'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Телефон</span>
                                <span className="detail-value">{formData.phoneNumber || '—'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Email</span>
                                <span className="detail-value">{user.email}</span>
                            </div>
                            {isStudent && (
                                <div className="detail-item">
                                    <span className="detail-label">Дата рождения</span>
                                    <span className="detail-value">{formData.dateOfBirth || '—'}</span>
                                </div>
                            )}
                        </div>
                    </div>
                ) : (
                    // Создание профиля для USER
                    <div className="profile-details">
                        <h3>Создание профиля</h3>
                        <p>Заполните данные, чтобы стать студентом или учителем:</p>
                        
                        <div className="form-grid" style={{ marginTop: '20px' }}>
                            <div className="form-group">
                                <label>Фамилия *</label>
                                <input
                                    type="text"
                                    value={formData.lastName}
                                    onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                                    className="form-input"
                                    placeholder="Введите фамилию"
                                />
                            </div>
                            <div className="form-group">
                                <label>Имя *</label>
                                <input
                                    type="text"
                                    value={formData.firstName}
                                    onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                                    className="form-input"
                                    placeholder="Введите имя"
                                />
                            </div>
                            <div className="form-group">
                                <label>Отчество</label>
                                <input
                                    type="text"
                                    value={formData.patronymic}
                                    onChange={(e) => setFormData({...formData, patronymic: e.target.value})}
                                    className="form-input"
                                    placeholder="Введите отчество"
                                />
                            </div>
                            <div className="form-group">
                                <label>Телефон</label>
                                <input
                                    type="tel"
                                    value={formData.phoneNumber}
                                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                                    className="form-input"
                                    placeholder="+79991234567"
                                />
                            </div>
                        </div>

                        <div className="profile-actions">
                            <button
                                onClick={handleCreateStudentProfile}
                                className="btn-primary"
                                disabled={loading || !formData.firstName || !formData.lastName}
                            >
                                {loading ? 'Создание...' : 'Стать студентом'}
                            </button>
                            <button
                                onClick={handleCreateTeacherProfile}
                                className="btn-secondary"
                                disabled={loading || !formData.firstName || !formData.lastName}
                            >
                                {loading ? 'Отправка...' : 'Стать учителем'}
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Profile;