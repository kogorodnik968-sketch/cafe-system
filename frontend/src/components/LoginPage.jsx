import { useState } from 'react';
import { Form, Input, Button, notification, Tabs } from 'antd';
import { UserOutlined, LockOutlined, PhoneOutlined, MailOutlined } from '@ant-design/icons';

export default function LoginPage({ onLogin }) {
  const [loading, setLoading] = useState(false);

  const handleLogin = async (values) => {
    setLoading(true);
    const res = await fetch(`${process.env.REACT_APP_API_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });
    if (res.ok) {
      const user = await res.json();
      notification.success({ message: `Добро пожаловать, ${user.firstName}!` });
      onLogin(user);
    } else {
      notification.error({ message: 'Неверный email или пароль' });
    }
    setLoading(false);
  };

  const handleRegister = async (values) => {
    setLoading(true);
    const res = await fetch(`${process.env.REACT_APP_API_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });
    if (res.ok) {
      const user = await res.json();
      notification.success({ message: `Регистрация успешна! Добро пожаловать, ${user.firstName}!` });
      onLogin(user);
    } else {
      notification.error({ message: 'Ошибка регистрации. Возможно, email уже занят.' });
    }
    setLoading(false);
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <div style={{
        width: '420px', padding: '40px', background: '#fff', borderRadius: '8px',
        boxShadow: '0 4px 20px rgba(0,0,0,0.15)'
      }}>
        <h1 style={{ textAlign: 'center', marginBottom: '28px', color: '#333' }}>☕ Café</h1>
        <Tabs centered items={[
          {
            key: 'login',
            label: 'Войти',
            children: (
              <Form onFinish={handleLogin} size="large">
                <Form.Item name="email" rules={[
                  { required: true, message: 'Введите email' },
                  { type: 'email', message: 'Некорректный email' },
                ]}>
                  <Input prefix={<MailOutlined />} placeholder="Email" />
                </Form.Item>
                <Form.Item name="password" rules={[
                  { required: true, message: 'Введите пароль' },
                ]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="Пароль" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" block loading={loading}>Войти</Button>
                </Form.Item>
              </Form>
            ),
          },
          {
            key: 'register',
            label: 'Регистрация',
            children: (
              <Form onFinish={handleRegister} size="large">
                <Form.Item name="firstName" rules={[{ required: true, message: 'Введите имя' }]}>
                  <Input prefix={<UserOutlined />} placeholder="Имя" />
                </Form.Item>
                <Form.Item name="lastName" rules={[{ required: true, message: 'Введите фамилию' }]}>
                  <Input prefix={<UserOutlined />} placeholder="Фамилия" />
                </Form.Item>
                <Form.Item name="phoneNumber" rules={[
                  { required: true, message: 'Введите телефон' },
                  { pattern: /^\+375\d{9}$/, message: 'Формат: +375XXXXXXXXX' },
                ]}>
                  <Input prefix={<PhoneOutlined />} placeholder="+375291234567" maxLength={13} />
                </Form.Item>
                <Form.Item name="email" rules={[
                  { required: true, message: 'Введите email' },
                  { type: 'email', message: 'Некорректный email' },
                ]}>
                  <Input prefix={<MailOutlined />} placeholder="Email" />
                </Form.Item>
                <Form.Item name="password" rules={[
                  { required: true, message: 'Введите пароль' },
                  { min: 6, message: 'Минимум 6 символов' },
                ]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="Пароль" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" block loading={loading}>Зарегистрироваться</Button>
                </Form.Item>
              </Form>
            ),
          },
        ]} />
      </div>
    </div>
  );
}