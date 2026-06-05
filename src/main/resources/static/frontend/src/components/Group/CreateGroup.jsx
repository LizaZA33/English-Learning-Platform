import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { groupService } from '../../services/groupService';
import ErrorMessage from '../Common/ErrorMessage';
import './CreateGroup.css';

const CreateGroup = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        groupName: '',
        teacherFullName: '',
        moduleName: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!formData.groupName.trim() || !formData.teacherFullName.trim() || !formData.moduleName.trim()) {
            setError('Заполните все обязательные поля');
            return;
        }

        setLoading(true);

        try {
            await groupService.create(formData);
            navigate('/groups');
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка создания группы');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="create-group-container">
            <div className="create-group-card">
                <h1>Создание учебной группы</h1>

                <ErrorMessage message={error} onClose={() => setError('')} />

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="groupName">Название группы</label>
                        <input
                            id="groupName"
                            name="groupName"
                            type="text"
                            value={formData.groupName}
                            onChange={handleChange}
                            placeholder="Например: Английский для начинающих"
                            className="form-input"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="teacherFullName">ФИО преподавателя</label>
                        <input
                            id="teacherFullName"
                            name="teacherFullName"
                            type="text"
                            value={formData.teacherFullName}
                            onChange={handleChange}
                            placeholder="Иванов Иван Иванович"
                            className="form-input"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="moduleName">Название модуля</label>
                        <input
                            id="moduleName"
                            name="moduleName"
                            type="text"
                            value={formData.moduleName}
                            onChange={handleChange}
                            placeholder="Базовый уровень"
                            className="form-input"
                        />
                    </div>

                    <div className="form-actions">
                        <button 
                            type="button" 
                            onClick={() => navigate('/groups')}
                            className="btn-cancel"
                        >
                            Отмена
                        </button>
                        <button 
                            type="submit" 
                            className="btn-primary"
                            disabled={loading}
                        >
                            {loading ? 'Создание...' : 'Создать группу'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateGroup;