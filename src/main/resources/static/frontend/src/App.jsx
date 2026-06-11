import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';
import Navbar from './components/Layout/Navbar';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import Profile from './components/Profile/Profile';
import FlashcardList from './components/Flashcard/FlashcardList';
import FlashcardSet from './components/Flashcard/FlashcardSet';
import CreateFlashcardSet from './components/Flashcard/CreateFlashcardSet';
import StudyMode from './components/Flashcard/StudyMode';
import JoinGroup from './components/Group/JoinGroup';
import GroupList from './components/Group/GroupList';
import CreateGroup from './components/Group/CreateGroup';
import LectureList from './components/Lecture/LectureList';
import LectureView from './components/Lecture/LectureView';
import LessonList from './components/Lesson/LessonList';
import LessonView from './components/Lesson/LessonView';
import AdminDashboard from './components/Dashboard/AdminDashboard';
import StudentDashboard from './components/Dashboard/StudentDashboard';
import TeacherDashboard from './components/Dashboard/TeacherDashboard';
import PrivateRoute from './components/Common/PrivateRoute';
import RoleBasedRedirect from './components/Common/RoleBasedRedirect';
import './App.css';

function App() {
    return (
        <ThemeProvider>
            <AuthProvider>
                <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                    <div className="app">
                        <Navbar />
                        <main className="main-content">
                            <Routes>
                                <Route path="/login" element={<Login />} />
                                <Route path="/register" element={<Register />} />
                                <Route path="/profile" element={
                                    <PrivateRoute>
                                        <Profile />
                                    </PrivateRoute>
                                } />
                                <Route path="/flashcards" element={
                                    <PrivateRoute>
                                        <FlashcardList />
                                    </PrivateRoute>
                                } />
                                <Route path="/flashcards/create" element={
                                    <PrivateRoute>
                                        <CreateFlashcardSet />
                                    </PrivateRoute>
                                } />
                                <Route path="/flashcards/:lessonId" element={
                                    <PrivateRoute>
                                        <FlashcardSet />
                                    </PrivateRoute>
                                } />
                                <Route path="/flashcards/:lessonId/study" element={
                                    <PrivateRoute>
                                        <StudyMode />
                                    </PrivateRoute>
                                } />
                                <Route path="/groups/join" element={
                                    <PrivateRoute>
                                        <JoinGroup />
                                    </PrivateRoute>
                                } />
                                <Route path="/groups" element={
                                    <PrivateRoute>
                                        <GroupList />
                                    </PrivateRoute>
                                } />
                                <Route path="/groups/create" element={
                                    <PrivateRoute>
                                        <CreateGroup />
                                    </PrivateRoute>
                                } />
                                <Route path="/lectures" element={
                                    <PrivateRoute>
                                        <LectureList />
                                    </PrivateRoute>
                                } />
                                <Route path="/lectures/:id" element={
                                    <PrivateRoute>
                                        <LectureView />
                                    </PrivateRoute>
                                } />
                                <Route path="/lessons" element={
                                    <PrivateRoute>
                                        <LessonList />
                                    </PrivateRoute>
                                } />
                                <Route path="/lessons/:id" element={
                                    <PrivateRoute>
                                        <LessonView />
                                    </PrivateRoute>
                                } />
                                <Route path="/admin" element={
                                    <PrivateRoute>
                                        <AdminDashboard />
                                    </PrivateRoute>
                                } />
                                <Route path="/dashboard/student" element={
                                    <PrivateRoute>
                                        <StudentDashboard />
                                    </PrivateRoute>
                                } />
                                <Route path="/dashboard/teacher" element={
                                    <PrivateRoute>
                                        <TeacherDashboard />
                                    </PrivateRoute>
                                } />
                                <Route path="/" element={<RoleBasedRedirect />} />
                            </Routes>
                        </main>
                    </div>
                </Router>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;