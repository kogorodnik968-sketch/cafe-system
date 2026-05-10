import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, Table, notification, Descriptions, Tag, Space } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ArrowLeftOutlined } from '@ant-design/icons';

const statusColors = {
  ACCEPTED: 'blue', PREPARING: 'orange', READY: 'green', DELIVERED: 'purple', CANCELLED: 'red',
};
const statusLabels = {
  ACCEPTED: 'Принят', PREPARING: 'Готовится', READY: 'Готов', DELIVERED: 'Выдан', CANCELLED: 'Отменён',
};

export default function CustomerManage() {
  const [customers, setCustomers] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [view, setView] = useState('list');
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [orders, setOrders] = useState([]);
  const [form] = Form.useForm();

  const loadCustomers = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/customers`);
    setCustomers(await res.json());
  };

  useEffect(() => { loadCustomers(); }, []);

  const openView = async (cust) => {
    setSelectedCustomer(cust);
    setView('detail');
    const res = await fetch(`${process.env.REACT_APP_API_URL}/orders/by-customer/${cust.id}`);
    setOrders(await res.json());
  };

  const openEdit = (cust) => {
    setSelectedCustomer(cust);
    form.setFieldsValue({
      firstName: cust.firstName,
      lastName: cust.lastName,
      middleName: cust.middleName,
      phoneNumber: cust.phoneNumber,
      imageUrl: cust.imageUrl,
    });
    setIsEditOpen(true);
  };

  const openCreate = () => {
    setSelectedCustomer(null);
    form.resetFields();
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = selectedCustomer
      ? `${process.env.REACT_APP_API_URL}/customers/${selectedCustomer.id}`
      : `${process.env.REACT_APP_API_URL}/customers`;
    const method = selectedCustomer ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        firstName: values.firstName,
        lastName: values.lastName,
        middleName: values.middleName,
        phoneNumber: values.phoneNumber,
        imageUrl: values.imageUrl,
      }),
    });

    if (res.ok) {
      notification.success({ message: selectedCustomer ? 'Клиент обновлён' : 'Клиент создан' });
      setIsEditOpen(false);
      loadCustomers();
      if (selectedCustomer) {
        setSelectedCustomer({ ...selectedCustomer, ...values });
      }
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  const handleDelete = async (id) => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/customers/${id}`, { method: 'DELETE' });
    if (res.ok) {
      notification.success({ message: 'Клиент удалён' });
      setView('list');
      loadCustomers();
    }
  };

  const orderColumns = [
    { title: 'Статус', dataIndex: 'status', key: 'status', render: (s) => <Tag color={statusColors[s]}>{statusLabels[s] || s}</Tag> },
    { title: 'Сумма', dataIndex: 'totalPrice', key: 'totalPrice', render: (v) => `${v} BYN` },
  ];

  const editModal = (
    <Modal
      title={selectedCustomer ? 'Редактировать клиента' : 'Новый клиент'}
      open={isEditOpen} onCancel={() => setIsEditOpen(false)} onOk={handleSave}
      okText="Сохранить" cancelText="Отмена"
    >
      <Form form={form} layout="vertical">
        <Form.Item name="firstName" label="Имя" rules={[{ required: true, message: 'Введите имя' }]}>
          <Input />
        </Form.Item>
        <Form.Item name="lastName" label="Фамилия" rules={[{ required: true, message: 'Введите фамилию' }]}>
          <Input />
        </Form.Item>
        <Form.Item name="middleName" label="Отчество"><Input /></Form.Item>
        <Form.Item name="phoneNumber" label="Телефон"><Input /></Form.Item>
        <Form.Item name="email" label="Email"><Input /></Form.Item>
        <Form.Item label="Фото">
          <Button
            icon={<PlusOutlined />}
            type="dashed"
            onClick={() => {
              const input = document.createElement('input');
              input.type = 'file';
              input.accept = 'image/*';
              input.onchange = async (e) => {
                const file = e.target.files[0];
                if (!file) return;
                const formData = new FormData();
                formData.append('file', file);
                const res = await fetch(`${process.env.REACT_APP_API_URL}/api/images/upload`, {
                  method: 'POST',
                  body: formData,
                });
                const data = await res.json();
                form.setFieldsValue({ imageUrl: data.url });
              };
              input.click();
            }}
          >
            Загрузить фото
          </Button>
        </Form.Item>
        <Form.Item name="imageUrl" hidden>
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );

  if (view === 'detail' && selectedCustomer) {
    return (
      <div>
        {editModal}
        <Button icon={<ArrowLeftOutlined />} onClick={() => { setView('list'); loadCustomers(); }} style={{ marginBottom: '20px' }}>
          Назад к клиентам
        </Button>

        <h2 style={{ marginBottom: '24px' }}>
          {selectedCustomer.lastName} {selectedCustomer.firstName} {selectedCustomer.middleName || ''}
        </h2>

        <div style={{ display: 'flex', gap: '32px', marginBottom: '32px', alignItems: 'flex-start' }}>
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px', flexShrink: 0 }}>
            {selectedCustomer.imageUrl ? (
              <img
                src={`${process.env.REACT_APP_API_URL}${selectedCustomer.imageUrl}`}
                alt="Фото"
                style={{ width: '200px', height: '250px', objectFit: 'cover', borderRadius: '8px' }}
              />
            ) : (
              <div style={{
                width: '200px', height: '250px', background: '#f5f5f5',
                borderRadius: '8px', display: 'flex', alignItems: 'center',
                justifyContent: 'center', color: '#bbb', fontSize: '14px'
              }}>
                🖼️ Фото
              </div>
            )}
            <Space>
              <Button icon={<EditOutlined />} onClick={() => openEdit(selectedCustomer)}>Редактировать</Button>
              <Button danger icon={<DeleteOutlined />} onClick={() => {
                Modal.confirm({
                  title: 'Удалить клиента?',
                  content: `Вы уверены, что хотите удалить "${selectedCustomer.lastName} ${selectedCustomer.firstName}"?`,
                  okText: 'Да', cancelText: 'Нет', centered: true,
                  onOk: () => handleDelete(selectedCustomer.id),
                });
              }}>Удалить</Button>
            </Space>
          </div>

          <div style={{ flex: 1 }}>
            <Descriptions column={1} bordered size="middle" style={{ maxWidth: '400px' }}>
              <Descriptions.Item label="Фамилия">{selectedCustomer.lastName}</Descriptions.Item>
              <Descriptions.Item label="Имя">{selectedCustomer.firstName}</Descriptions.Item>
              <Descriptions.Item label="Отчество">{selectedCustomer.middleName || '—'}</Descriptions.Item>
              <Descriptions.Item label="Телефон">{selectedCustomer.phoneNumber || '—'}</Descriptions.Item>
              <Descriptions.Item label="Email">{selectedCustomer.email || '—'}</Descriptions.Item>
            </Descriptions>
          </div>
        </div>

        <h3>Заказы клиента</h3>
        {orders.length === 0 ? (
          <p>Нет заказов</p>
        ) : (
          <Table dataSource={orders} columns={orderColumns} rowKey="id" pagination={{ pageSize: 10 }} style={{ maxWidth: '600px' }} />
        )}
      </div>
    );
  }

  return (
    <div>
      {editModal}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
        <h2>Клиенты</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Добавить клиента</Button>
      </div>

     <Row gutter={[16, 16]}>
       {customers.map((cust) => (
         <Col xs={24} sm={12} md={8} lg={6} key={cust.id}>
           <Card hoverable style={{ height: '100%' }} onClick={() => openView(cust)}>
             <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
               {cust.imageUrl ? (
                 <img
                   src={`${process.env.REACT_APP_API_URL}${cust.imageUrl}`}
                   alt="Фото"
                   style={{ width: '50px', height: '50px', borderRadius: '50%', objectFit: 'cover', flexShrink: 0 }}
                 />
               ) : (
                 <div style={{
                   width: '50px', height: '50px', borderRadius: '50%', background: '#f0f0f0',
                   display: 'flex', alignItems: 'center', justifyContent: 'center',
                   color: '#bbb', fontSize: '20px', flexShrink: 0
                 }}>
                   {cust.firstName?.charAt(0)}
                 </div>
               )}
               <div>
                 <div style={{ fontWeight: 'bold' }}>{cust.lastName} {cust.firstName}</div>
                 <div style={{ color: '#888' }}>{cust.phoneNumber || '—'}</div>
               </div>
             </div>
           </Card>
         </Col>
       ))}
     </Row>
    </div>
  );
}