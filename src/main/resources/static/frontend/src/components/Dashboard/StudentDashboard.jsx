import React, { useState, useEffect } from 'react';
import { progressService } from '../../services/progressService';
import ErrorMessage from '../Common/ErrorMessage';
import './StudentDashboard.css';

const StudentDashboard = () => {
    const [lectureProgress, setLectureProgress] = useState([]);
    const [lessonProgress, setLessonProgress] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadProgress();
    }, []);

    const loadProgress = async () => {
        try {
            const [lectureRes, lessonRes] = await Promise.all([
                progressService.getLectureProgress(),
                progressService.getLessonProgress()
            ]);
            setLectureProgress(lectureRes.data.content || []);
            setLessonProgress(lessonRes.data.content || []);
        } catch (err) {
            setError('Ошибка загрузки прогресса');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка прогресса...</div>;
    }

    return (
        <div className="student-dashboard">
            <h1>Мой прогресс</h1>

            <ErrorMessage message={error} onClose={() => setError('')} />

            <div className="progress-sections">
                <div className="progress-section">
                    <h2>Прогресс по лекциям</h2>
                    {lectureProgress.length === 0 ? (
                        <p className="no-data">Нет данных о прогрессе по лекциям</p>
                    ) : (
                        <div className="progress-list">
                            {lectureProgress.map(item => (
                                <div key={item.lectureId} className="progress-item">
                                    <div className="progress-item-header">
                                        <span>{item.lectureTitle}</span>
                                        <span className="progress-value">{item.lectureProgress}%</span>
                                    </div>
                                    <div className="progress-bar">
                                        <div 
                                            className="progress-fill"
                                            style={{ width: `${item.lectureProgress}%` }}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="progress-section">
                    <h2>Прогресс по урокам</h2>
                    {lessonProgress.length === 0 ? (
                        <p className="no-data">Нет данных о прогрессе по урокам</p>
                    ) : (
                        <div className="progress-list">
                            {lessonProgress.map(item => (
                                <div key={item.lessonId} className="progress-item">
                                    <div className="progress-item-header">
                                        <span>{item.lessonTitle}</span>
                                        <span className="progress-value">{item.lessonProgress}%</span>
                                    </div>
                                    <div className="progress-bar">
                                        <div 
                                            className="progress-fill"
                                            style={{ width: `${item.lessonProgress}%` }}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default StudentDashboard;