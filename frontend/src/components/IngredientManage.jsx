import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, notification, Descriptions } from 'antd';
import { PlusOutlined, EditOutlined} from '@ant-design/icons';

export default function IngredientManage() {
  const [ingredients, setIngredients] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedIngredient, setSelectedIngredient] = useState(null);
  const [form] = Form.useForm();

  const loadData = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/ingredients`);
    setIngredients(await res.json());
  };

  useEffect(() => { loadData(); }, []);

  const openView = (ing) => {
    setSelectedIngredient(ing);
    setIsViewOpen(true);
  };

  const openEdit = (ing) => {
    setSelectedIngredient(ing);
    form.setFieldsValue({ name: ing.name });
    setIsEditOpen(true);
  };

  const openCreate = () => {
    setSelectedIngredient(null);
    form.resetFields();
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = selectedIngredient
      ? `${process.env.REACT_APP_API_URL}/ingredients/${selectedIngredient.id}`
      : `${process.env.REACT_APP_API_URL}/ingredients`;
    const method = selectedIngredient ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });

    if (res.ok) {
      notification.success({ message: selectedIngredient ? 'Ингредиент обновлён' : 'Ингредиент создан' });
      setIsEditOpen(false);
      loadData();
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
        <h2>Ингредиенты</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Добавить ингредиент</Button>
      </div>

      <Row gutter={[16, 16]}>
        {ingredients.map((ing) => (
          <Col xs={24} sm={12} md={8} lg={6} key={ing.id}>
            <Card
              hoverable
              style={{ height: '100%' }}
              onClick={() => openView(ing)}
              actions={[
                <EditOutlined key="edit" onClick={() => openEdit(ing)} />,
              ]}
            >
              <Card.Meta title={ing.name} />
            </Card>
          </Col>
        ))}
      </Row>

      <Modal
        title={selectedIngredient?.name}
        open={isViewOpen}
        onCancel={() => setIsViewOpen(false)}
        footer={<Button onClick={() => setIsViewOpen(false)}>Закрыть</Button>}
      >
        {selectedIngredient && (
          <Descriptions column={1} bordered size="middle">

            <Descriptions.Item label="Название">{selectedIngredient.name}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      <Modal
        title={selectedIngredient ? 'Редактировать ингредиент' : 'Новый ингредиент'}
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