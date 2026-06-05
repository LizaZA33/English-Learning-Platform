import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { lectureService } from '../../services/lectureService';
import { useAuth } from '../../contexts/AuthContext';
import ErrorMessage from '../Common/ErrorMessage';
import './LectureView.css';

const LectureView = () => {
    const { id } = useParams();
    const { hasRole } = useAuth();
    const [lecture, setLecture] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadLecture();
    }, [id]);

    const loadLecture = async () => {
        try {
            const response = await lectureService.getById(id);
            setLecture(response.data);
        } catch (err) {
            setError('Ошибка загрузки лекции');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Загрузка лекции...</div>;
    }

    if (!lecture) {
        return <div className="loading">Лекция не найдена</div>;
    }

    return (
        <div className="lecture-view-container">
            <div className="lecture-breadcrumb">
                <Link to="/lectures">Лекции</Link>
                <span>/</span>
                <span>{lecture.title}</span>
            </div>

            <ErrorMessage message={error} />

            <article className="lecture-content">
                <h1>{lecture.title}</h1>
                {lecture.module && (
                    <div className="lecture-meta">
                        <span className="lecture-module-badge">{lecture.module.name}</span>
                    </div>
                )}
                <div className="lecture-body">
                    {lecture.content?.split('\n').map((paragraph, index) => (
                        <p key={index}>{paragraph}</p>
                    ))}
                </div>
            </article>
        </div>
    );
};

export default LectureView;