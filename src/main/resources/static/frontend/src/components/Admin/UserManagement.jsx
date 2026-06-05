import React, { useState, useEffect } from 'react';
import userService from '../../services/userService';
import './UserManagement.css';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const response = await userService.getAllUsers();
      if (response && response.content) {
        setUsers(response.content);
      }
    } catch (err) {
      setError('Ошибка загрузки пользователей');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (window.confirm('Вы уверены, что хотите удалить этого пользователя?')) {
      try {
        await userService.deleteUser(userId);
        loadUsers();
      } catch (err) {
        setError('Ошибка при удалении пользователя');
      }
    }
  };

  if (loading) return <div className="text-center">Загрузка...</div>;

  return (
    <div className="user-management">
      <h1>Управление пользователями</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="users-table-container">
        <table className="users-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Email</th>
              <th>Роли</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.email}</td>
                <td>{user.roles?.join(', ') || 'USER'}</td>
                <td>
                  <button
                    onClick={() => handleDeleteUser(user.id)}
                    className="btn-danger-sm"
                  >
                    Удалить
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserManagement;