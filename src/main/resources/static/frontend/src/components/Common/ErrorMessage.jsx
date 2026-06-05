import React from 'react';
import './ErrorMessage.css';

const ErrorMessage = ({ message, onClose }) => {
    if (!message) return null;

    return (
        <div className="error-message">
            <span className="error-text">{message}</span>
            {onClose && (
                <button className="error-close" onClick={onClose}>
                    X
                </button>
            )}
        </div>
    );
};

export default ErrorMessage;