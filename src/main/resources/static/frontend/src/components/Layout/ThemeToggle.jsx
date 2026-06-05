import React from 'react';
import { useTheme } from '../../contexts/ThemeContext';
import './ThemeToggle.css';

const ThemeToggle = () => {
    const { theme, toggleTheme } = useTheme();

    return (
        <button 
            className={`theme-toggle ${theme}`}
            onClick={toggleTheme}
            title={theme === 'light' ? 'Переключить на тёмную тему' : 'Переключить на светлую тему'}
        >
            <span className="theme-icon">
                {theme === 'light' ? '◉' : '☉'}
            </span>
        </button>
    );
};

export default ThemeToggle;