import React from 'react';
import { Link } from 'react-router-dom';
import './TeacherDashboard.css';

const TeacherDashboard = () => {
    return (
        <div className="teacher-dashboard">
            <h1>Панель учителя</h1>

            <div className="teacher-cards">
                <Link to="/groups/create" className="teacher-card">
                    <h3>Создать группу</h3>
                    <p>Создайте новую учебную группу и пригласите студентов</p>
                </Link>

                <Link to="/groups" className="teacher-card">
                    <h3>Мои группы</h3>
                    <p>Просмотр и управление учебными группами</p>
                </Link>

                <Link to="/lectures" className="teacher-card">
                    <h3>Лекции</h3>
                    <p>Создание и управление лекционным материалом</p>
                </Link>

                <Link to="/lessons" className="teacher-card">
                    <h3>Уроки</h3>
                    <p>Создание уроков с карточками для студентов</p>
                </Link>
            </div>
        </div>
    );
};

export default TeacherDashboard;