import React, { useState, useEffect } from 'react';
import userService from '../../services/userService';
import './RoleManagement.css';

const RoleManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState('');
  const [action, setAction] = useState('add');

  const roles = ['STUDENT', 'TEACHER', 'ADMIN'];

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

  const handleRoleChange = async () => {
    if (!selectedUser || !selectedRole) {
      setError('Выберите пользователя и роль');
      return;
    }

    try {
      await userService.updateUserRole(selectedUser.id, selectedRole, action === 'add');
      alert(`Роль ${action === 'add' ? 'добавлена' : 'удалена'} успешно`);
      loadUsers();
      setSelectedUser(null);
      setSelectedRole('');
    } catch (err) {
      setError('Ошибка при изменении роли');
    }
  };

  if (loading) return <div className="text-center">Загрузка...</div>;

  return (
    <div className="role-management">
      <h1>Управление ролями</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="role-form">
        <h2>Назначение ролей</h2>
        <div className="form-group">
          <label>Выберите пользователя</label>
          <select onChange={(e) => {
            const user = users.find(u => u.id === parseInt(e.target.value));
            setSelectedUser(user);
          }} value={selectedUser?.id || ''}>
            <option value="">Выберите пользователя</option>
            {users.map(user => (
              <option key={user.id} value={user.id}>
                {user.email} (Роли: {user.roles?.join(', ') || 'USER'})
              </option>
            ))}
          </select>
        </div>
        
        <div className="form-group">
          <label>Роль</label>
          <select onChange={(e) => setSelectedRole(e.target.value)} value={selectedRole}>
            <option value="">Выберите роль</option>
            {roles.map(role => (
              <option key={role} value={role}>{role}</option>
            ))}
          </select>
        </div>
        
        <div className="form-group">
          <label>Действие</label>
          <div className="radio-group">
            <label>
              <input
                type="radio"
                value="add"
                checked={action === 'add'}
                onChange={(e) => setAction(e.target.value)}
              />
              Добавить роль
            </label>
            <label>
              <input
                type="radio"
                value="remove"
                checked={action === 'remove'}
                onChange={(e) => setAction(e.target.value)}
              />
              Удалить роль
            </label>
          </div>
        </div>
        
        <button onClick={handleRoleChange} className="btn btn-primary">
          Применить изменения
        </button>
      </div>
      
      <div className="users-list">
        <h2>Список пользователей</h2>
        <div className="users-table-container">
          <table className="users-table">
            <thead>
              <tr>
                <th>Email</th>
                <th>Роли</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td>{user.email}</td>
                  <td>{user.roles?.join(', ') || 'USER'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default RoleManagement;