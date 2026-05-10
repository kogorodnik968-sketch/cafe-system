import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, Table, notification, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

export default function TagManage() {
  const [tags, setTags] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedTag, setSelectedTag] = useState(null);
  const [products, setProducts] = useState([]);
  const [form] = Form.useForm();

  const loadTags = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/tag`);
    setTags(await res.json());
  };

  useEffect(() => { loadTags(); }, []);

  const openView = async (tag) => {
    setSelectedTag(tag);
    setIsViewOpen(true);
    const res = await fetch(`${process.env.REACT_APP_API_URL}/products/by-tag/${tag.id}`);
    setProducts(await res.json());
  };

  const openEdit = (tag) => {
    setSelectedTag(tag);
    form.setFieldsValue({ name: tag.name });
    setIsEditOpen(true);
  };

  const openCreate = () => {
    setSelectedTag(null);
    form.resetFields();
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = selectedTag
      ? `${process.env.REACT_APP_API_URL}/tag/${selectedTag.id}`
      : `${process.env.REACT_APP_API_URL}/tag`;
    const method = selectedTag ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });

    if (res.ok) {
      notification.success({ message: selectedTag ? 'Тег обновлён' : 'Тег создан' });
      setIsEditOpen(false);
      loadTags();
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  const handleDelete = async (id) => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/tag/${id}`, { method: 'DELETE' });
    if (res.ok) {
      notification.success({ message: 'Тег удалён' });
      loadTags();
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
    { title: 'Категория', dataIndex: 'categoryName', key: 'categoryName', render: (v) => <Tag color="blue">{v}</Tag> },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
        <h2>Теги</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Добавить тег</Button>
      </div>

      <Row gutter={[16, 16]}>
        {tags.map((tag) => (
          <Col xs={24} sm={12} md={8} lg={6} key={tag.id}>
            <Card
              hoverable
              style={{ height: '100%' }}
              onClick={() => openView(tag)}
              actions={[
                <EditOutlined key="edit" onClick={(e) => { e.stopPropagation(); openEdit(tag); }} />,
                <DeleteOutlined
                  key="delete"
                  style={{ color: 'red' }}
                  onClick={(e) => {
                    e.stopPropagation();
                    Modal.confirm({
                      title: 'Удалить тег?',
                      content: `Вы уверены, что хотите удалить "${tag.name}"?`,
                      okText: 'Да',
                      cancelText: 'Нет',
                      centered: true,
                      onOk: () => handleDelete(tag.id),
                    });
                  }}
                />,
              ]}
            >
              <Card.Meta title={tag.name} />
            </Card>
          </Col>
        ))}
      </Row>

      {/* Просмотр тега + товары */}
      <Modal
        title={`Тег: ${selectedTag?.name}`}
        open={isViewOpen}
        onCancel={() => setIsViewOpen(false)}
        footer={<Button onClick={() => setIsViewOpen(false)}>Закрыть</Button>}
        width={700}
      >
        {products.length === 0 ? (
          <p>Нет товаров с этим тегом</p>
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
        title={selectedTag ? 'Редактировать тег' : 'Новый тег'}
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