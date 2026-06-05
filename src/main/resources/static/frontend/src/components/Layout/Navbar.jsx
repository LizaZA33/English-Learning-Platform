import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';
import ThemeToggle from './ThemeToggle';
import './Navbar.css';

const Navbar = () => {
    const { user, logout, isAuthenticated, hasRole } = useAuth();
    const { theme } = useTheme();
    const navigate = useNavigate();
    const [menuOpen, setMenuOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className={`navbar ${theme}`}>
            <div className="navbar-container">
                <Link to="/" className="navbar-logo">
                    EnglishLearn
                </Link>

                <button 
                    className="menu-toggle"
                    onClick={() => setMenuOpen(!menuOpen)}
                >
                    <span className="menu-icon"></span>
                </button>

                <div className={`navbar-menu ${menuOpen ? 'active' : ''}`}>
                    {isAuthenticated && (
                        <>
                            <Link to="/flashcards" className="nav-link">Карточки</Link>
                            
                            {(hasRole('STUDENT') || hasRole('TEACHER')) && (
                                <Link to="/lectures" className="nav-link">Лекции</Link>
                            )}
                            
                            {(hasRole('STUDENT') || hasRole('TEACHER')) && (
                                <Link to="/lessons" className="nav-link">Уроки</Link>
                            )}
                            
                            {!hasRole('STUDENT') && !hasRole('TEACHER') && (
                                <Link to="/groups/join" className="nav-link">Вступить в группу</Link>
                            )}
                            
                            {(hasRole('STUDENT') || hasRole('TEACHER')) && (
                                <Link to="/groups" className="nav-link">Группы</Link>
                            )}
                            
                            {hasRole('STUDENT') && (
                                <Link to="/dashboard/student" className="nav-link">Прогресс</Link>
                            )}
                            
                            {hasRole('TEACHER') && (
                                <Link to="/dashboard/teacher" className="nav-link">Панель учителя</Link>
                            )}
                            
                            {hasRole('ADMIN') && (
                                <Link to="/admin" className="nav-link admin-link">Админ-панель</Link>
                            )}
                        </>
                    )}

                    <div className="navbar-actions">
                        {isAuthenticated && (
                            <Link to="/profile" className="nav-link">Профиль</Link>
                        )}
                        <ThemeToggle />
                        {isAuthenticated ? (
                            <button onClick={handleLogout} className="btn-logout">
                                Выйти
                            </button>
                        ) : (
                            <Link to="/login" className="btn-login">
                                Войти
                            </Link>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;