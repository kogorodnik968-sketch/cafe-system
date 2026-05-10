import { useState, useEffect } from 'react';
import { Descriptions, Button, Table, Tag, notification, Modal, Form, Input } from 'antd';
import { EditOutlined, PlusOutlined } from '@ant-design/icons';

const statusColors = {
  ACCEPTED: 'blue', PREPARING: 'orange', READY: 'green', DELIVERED: 'purple', CANCELLED: 'red',
};
const statusLabels = {
  ACCEPTED: 'Принят', PREPARING: 'Готовится', READY: 'Готов', DELIVERED: 'Выдан', CANCELLED: 'Отменён',
};

export default function ProfilePage({ user }) {
  const [profile, setProfile] = useState(null);
  const [orders, setOrders] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [form] = Form.useForm();

  const loadProfile = async () => {
    const url = user.role === 'CUSTOMER'
      ? `${process.env.REACT_APP_API_URL}/customers/${user.id}`
      : `${process.env.REACT_APP_API_URL}/employees/${user.id}`;
    const res = await fetch(url);
    if (res.ok) setProfile(await res.json());
  };

  const loadOrders = async () => {
    if (user.role === 'CUSTOMER') {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/orders/by-customer/${user.id}`);
      if (res.ok) setOrders(await res.json());
    }
  };

  useEffect(() => {
    loadProfile();
    loadOrders();
    // eslint-disable-next-line
  }, [user]);

  const uploadPhoto = async () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.onchange = async (e) => {
      const file = e.target.files[0];
      if (!file) return;
      const formData = new FormData();
      formData.append('file', file);
      const uploadRes = await fetch(`${process.env.REACT_APP_API_URL}/api/images/upload`, {
        method: 'POST', body: formData,
      });
      const data = await uploadRes.json();
      form.setFieldsValue({ imageUrl: data.url });
      notification.success({ message: 'Фото выбрано! Сохраните изменения.' });
    };
    input.click();
  };

  const openEdit = () => {
    form.setFieldsValue({
      firstName: profile.firstName,
      lastName: profile.lastName,
      middleName: profile.middleName || '',
      phoneNumber: profile.phoneNumber || '',
      imageUrl: profile.imageUrl || '',
    });
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = user.role === 'CUSTOMER'
      ? `${process.env.REACT_APP_API_URL}/customers/${user.id}`
      : `${process.env.REACT_APP_API_URL}/employees/${user.id}`;

    const body = {
      firstName: values.firstName,
      lastName: values.lastName,
      middleName: values.middleName || '',
      phoneNumber: values.phoneNumber || '',
      email: profile.email || '',
      password: profile.password || '',
      imageUrl: values.imageUrl || profile.imageUrl || '',
    };
    if (user.role !== 'CUSTOMER') body.role = profile.role;

    const res = await fetch(url, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });

    if (res.ok) {
      notification.success({ message: 'Данные обновлены!' });
      setIsEditOpen(false);
      loadProfile();
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  if (!profile) return <p>Загрузка...</p>;

  return (
    <div style={{ maxWidth: '700px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h2>Мой профиль</h2>

      </div>

      <div style={{ display: 'flex', gap: '32px', marginBottom: '32px', alignItems: 'flex-start' }}>
        <div style={{ flexShrink: 0, textAlign: 'center' }}>
          {profile.imageUrl ? (
            <img src={`${process.env.REACT_APP_API_URL}${profile.imageUrl}`} alt="Фото"
              style={{ width: '180px', height: '220px', objectFit: 'cover', borderRadius: '8px' }} />
          ) : (
            <div style={{
              width: '180px', height: '220px', background: '#f5f5f5', borderRadius: '8px',
              display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#bbb', fontSize: '48px'
            }}>
              {profile.firstName?.charAt(0)}
            </div>
          )}
       <Button icon={<EditOutlined />} style={{ marginTop: '12px', width: '100%' }} onClick={openEdit}>
          Редактировать
        </Button>
        </div>

        <Descriptions column={1} bordered size="middle"  style={{ minWidth: '350px' }}>
          <Descriptions.Item label="Имя">{profile.firstName}</Descriptions.Item>
          <Descriptions.Item label="Фамилия">{profile.lastName}</Descriptions.Item>
          <Descriptions.Item label="Отчество">{profile.middleName || '—'}</Descriptions.Item>
          <Descriptions.Item label="Телефон">{profile.phoneNumber || '—'}</Descriptions.Item>
          <Descriptions.Item label="Email">{profile.email || '—'}</Descriptions.Item>
          {profile.role && <Descriptions.Item label="Роль"><Tag color="blue">{profile.role}</Tag></Descriptions.Item>}
        </Descriptions>
      </div>

      {user.role === 'CUSTOMER' && (
        <>
          <h3>Мои заказы</h3>
          {orders.length === 0 ? (
            <p>Нет заказов</p>
          ) : (
            <Table dataSource={orders} rowKey="id" pagination={false}
              columns={[
                { title: 'Статус', dataIndex: 'status', render: (s) => <Tag color={statusColors[s]}>{statusLabels[s] || s}</Tag> },
                { title: 'Сумма', dataIndex: 'totalPrice', render: (v) => `${v} BYN` },
              ]}
            />
          )}
        </>
      )}

      {/* Модалка редактирования */}
      <Modal
        title="Редактировать профиль"
        open={isEditOpen}
        onCancel={() => setIsEditOpen(false)}
        onOk={handleSave}
        okText="Сохранить"
        cancelText="Отмена"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="firstName" label="Имя" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="lastName" label="Фамилия" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="middleName" label="Отчество">
            <Input />
          </Form.Item>
          <Form.Item name="phoneNumber" label="Телефон">
            <Input />
          </Form.Item>
          <Form.Item label="Фото">
            <Button icon={<PlusOutlined />} type="dashed" onClick={uploadPhoto}>
              Изменить фото
            </Button>
          </Form.Item>
          <Form.Item name="imageUrl" hidden>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}