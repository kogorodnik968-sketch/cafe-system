import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, Table, notification, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

export default function CategoryManage() {
  const [categories, setCategories] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [products, setProducts] = useState([]);
  const [form] = Form.useForm();

  const loadCategories = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/category`);
    setCategories(await res.json());
  };

  useEffect(() => { loadCategories(); }, []);

  const openView = async (cat) => {
    setSelectedCategory(cat);
    setIsViewOpen(true);
    const res = await fetch(`${process.env.REACT_APP_API_URL}/products/by-category/${cat.id}`);
    setProducts(await res.json());
  };

  const openEdit = (cat) => {
    setSelectedCategory(cat);
    form.setFieldsValue({ name: cat.name });
    setIsEditOpen(true);
  };

  const openCreate = () => {
    setSelectedCategory(null);
    form.resetFields();
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = selectedCategory
      ? `${process.env.REACT_APP_API_URL}/category/${selectedCategory.id}`
      : `${process.env.REACT_APP_API_URL}/category`;
    const method = selectedCategory ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });

    if (res.ok) {
      notification.success({ message: selectedCategory ? 'Категория обновлена' : 'Категория создана' });
      setIsEditOpen(false);
      loadCategories();
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  const handleDelete = async (id) => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/category/${id}`, { method: 'DELETE' });
    if (res.ok) {
      notification.success({ message: 'Категория удалена' });
     loadCategories();
    } else {
      notification.error({ message: 'Ошибка при удалении' });
    }
  };

  const columns = [
    {
      title: '',
      dataIndex: 'image',
      key: 'image',
      width: 60,
      render: () => (
        <div style={{ width: '40px', height: '40px', background: '#f5f5f5', borderRadius: '4px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#bbb', fontSize: '12px' }}>
          🖼️
        </div>
      ),
    },
    { title: 'Название', dataIndex: 'name', key: 'name' },
    { title: 'Цена', dataIndex: 'price', key: 'price', render: (v) => `${v} BYN` },
    { title: 'Тег', dataIndex: 'tagName', key: 'tagName', render: (v) => <Tag color="orange">{v}</Tag> },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
        <h2>Категории</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Добавить категорию</Button>
      </div>

      <Row gutter={[16, 16]}>
        {categories.map((cat) => (
          <Col xs={24} sm={12} md={8} lg={6} key={cat.id}>
            <Card
              hoverable
              style={{ height: '100%' }}
              onClick={() => openView(cat)}
              actions={[
                <EditOutlined key="edit" onClick={(e) => { e.stopPropagation(); openEdit(cat); }} />,
                <DeleteOutlined
                  key="delete"
                  style={{ color: 'red' }}
                  onClick={(e) => {
                    e.stopPropagation();
                    Modal.confirm({
                      title: 'Удалить категорию?',
                      content: `Вы уверены, что хотите удалить "${cat.name}"?`,
                      okText: 'Да',
                      cancelText: 'Нет',
                      centered: true,
                      onOk: () => handleDelete(cat.id),
                    });
                  }}
                />,
              ]}
            >
              <Card.Meta title={cat.name} />
            </Card>
          </Col>
        ))}
      </Row>

      {/* Просмотр категории + товары */}
      <Modal
        title={`Категория: ${selectedCategory?.name}`}
        open={isViewOpen}
        onCancel={() => setIsViewOpen(false)}
        footer={<Button onClick={() => setIsViewOpen(false)}>Закрыть</Button>}
        width={700}
      >
        {products.length === 0 ? (
          <p>Нет товаров в этой категории</p>
        ) : (
          <Table
            dataSource={products}
            columns={columns}
            rowKey="id"
            pagination={false}
            size="small"
          />
        )}
      </Modal>

      {/* Создание / Редактирование */}
      <Modal
        title={selectedCategory ? 'Редактировать категорию' : 'Новая категория'}
        open={isEditOpen}
        onCancel={() => setIsEditOpen(false)}
        onOk={handleSave}
        okText="Сохранить"
        cancelText="Отмена"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="Название" rules={[{ required: true, message: 'Введите название' }]}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}