import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, Select, notification, Descriptions, Tag, Space } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ArrowLeftOutlined } from '@ant-design/icons';

const roleLabels = {
  BARISTA: 'Бариста',
  WAITER: 'Официант',
  MANAGER: 'Менеджер',
  ADMIN: 'Администратор',
};

export default function EmployeeManage() {
  const [employees, setEmployees] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [view, setView] = useState('list');
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [form] = Form.useForm();

  const loadEmployees = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/employees`);
    setEmployees(await res.json());
  };

  useEffect(() => { loadEmployees(); }, []);

  const openView = (emp) => {
    setSelectedEmployee(emp);
    setView('detail');
  };

  const openEdit = (emp) => {
    setSelectedEmployee(emp);
    form.setFieldsValue({
      firstName: emp.firstName,
      lastName: emp.lastName,
      middleName: emp.middleName,
      role: emp.role,
      imageUrl: emp.imageUrl,
    });
    setIsEditOpen(true);
  };

  const openCreate = () => {
    setSelectedEmployee(null);
    form.resetFields();
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = selectedEmployee
      ? `${process.env.REACT_APP_API_URL}/employees/${selectedEmployee.id}`
      : `${process.env.REACT_APP_API_URL}/employees`;
    const method = selectedEmployee ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        firstName: values.firstName,
        lastName: values.lastName,
        middleName: values.middleName,
        role: values.role,
        imageUrl: values.imageUrl,
      }),
    });

    if (res.ok) {
      notification.success({ message: selectedEmployee ? 'Сотрудник обновлён' : 'Сотрудник создан' });
      setIsEditOpen(false);
      loadEmployees();
      if (selectedEmployee) {
        setSelectedEmployee({ ...selectedEmployee, ...values });
      }
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  const handleDelete = async (id) => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/employees/${id}`, { method: 'DELETE' });
    if (res.ok) {
      notification.success({ message: 'Сотрудник удалён' });
      setView('list');
      loadEmployees();
    }
  };

  const editModal = (
    <Modal
      title={selectedEmployee ? 'Редактировать сотрудника' : 'Новый сотрудник'}
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
        <Form.Item name="role" label="Роль">
          <Select placeholder="Выберите роль" allowClear>
            <Select.Option value="BARISTA">Бариста</Select.Option>
            <Select.Option value="WAITER">Официант</Select.Option>
            <Select.Option value="MANAGER">Менеджер</Select.Option>
            <Select.Option value="COOK">Повар</Select.Option>
          </Select>
        </Form.Item>
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

  if (view === 'detail' && selectedEmployee) {
    return (
      <div>
        {editModal}
        <Button icon={<ArrowLeftOutlined />} onClick={() => { setView('list'); loadEmployees(); }} style={{ marginBottom: '20px' }}>
          Назад к сотрудникам
        </Button>

        <h2 style={{ marginBottom: '24px' }}>
          {selectedEmployee.lastName} {selectedEmployee.firstName} {selectedEmployee.middleName || ''}
        </h2>

        <div style={{ display: 'flex', gap: '32px', marginBottom: '32px', alignItems: 'flex-start' }}>
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px', flexShrink: 0 }}>
            {selectedEmployee.imageUrl ? (
              <img
                src={`${process.env.REACT_APP_API_URL}${selectedEmployee.imageUrl}`}
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
              <Button icon={<EditOutlined />} onClick={() => openEdit(selectedEmployee)}>Редактировать</Button>
              {selectedEmployee.role !== 'ADMIN' && (
                <Button danger icon={<DeleteOutlined />} onClick={() => {
                  Modal.confirm({
                    title: 'Удалить сотрудника?',
                    content: `Вы уверены, что хотите удалить "${selectedEmployee.lastName} ${selectedEmployee.firstName}"?`,
                    okText: 'Да', cancelText: 'Нет', centered: true,
                    onOk: () => handleDelete(selectedEmployee.id),
                  });
                }}>Удалить</Button>
              )}
            </Space>
          </div>

          <div style={{ flex: 1 }}>
            <Descriptions column={1} bordered size="middle" style={{ maxWidth: '400px' }}>
              <Descriptions.Item label="Фамилия">{selectedEmployee.lastName}</Descriptions.Item>
              <Descriptions.Item label="Имя">{selectedEmployee.firstName}</Descriptions.Item>
              <Descriptions.Item label="Отчество">{selectedEmployee.middleName || '—'}</Descriptions.Item>
              <Descriptions.Item label="Роль">
                <Tag color="blue">{roleLabels[selectedEmployee.role] || selectedEmployee.role || '—'}</Tag>
              </Descriptions.Item>
            </Descriptions>
          </div>
        </div>


      </div>
    );
  }

  return (
    <div>
      {editModal}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
        <h2>Сотрудники</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Добавить сотрудника</Button>
      </div>

      <Row gutter={[16, 16]}>
        {employees.map((emp) => (
          <Col xs={24} sm={12} md={8} lg={6} key={emp.id}>
            <Card hoverable style={{ height: '100%' }} onClick={() => openView(emp)}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                {emp.imageUrl ? (
                  <img
                    src={`${process.env.REACT_APP_API_URL}${emp.imageUrl}`}
                    alt="Фото"
                    style={{ width: '50px', height: '50px', borderRadius: '50%', objectFit: 'cover', flexShrink: 0 }}
                  />
                ) : (
                  <div style={{
                    width: '50px', height: '50px', borderRadius: '50%', background: '#f0f0f0',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    color: '#bbb', fontSize: '20px', flexShrink: 0
                  }}>
                    {emp.firstName?.charAt(0)}
                  </div>
                )}
                <div>
                  <div style={{ fontWeight: 'bold' }}>{emp.lastName} {emp.firstName}</div>
                  <div style={{ color: '#888' }}>{roleLabels[emp.role] || emp.role || '—'}</div>
                </div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  );
}