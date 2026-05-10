import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, InputNumber, Select, notification, Tag, Descriptions } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

export default function ProductManage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [ingredients, setIngredients] = useState([]);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [form] = Form.useForm();
  const [filterCategory, setFilterCategory] = useState(null);

  const loadData = async () => {
    const [prodRes, catRes, tagRes, ingRes] = await Promise.all([
      fetch(`${process.env.REACT_APP_API_URL}/products`),
      fetch(`${process.env.REACT_APP_API_URL}/category`),
      fetch(`${process.env.REACT_APP_API_URL}/tag`),
      fetch(`${process.env.REACT_APP_API_URL}/ingredients`),
    ]);
    setProducts(await prodRes.json());
    setCategories(await catRes.json());
    setTags(await tagRes.json());
    setIngredients(await ingRes.json());
  };

  useEffect(() => { loadData(); }, []);

  const openView = (product) => {
    setSelectedProduct(product);
    setIsViewOpen(true);
  };

  const openEdit = (product) => {
    setSelectedProduct(product);
    form.setFieldsValue({
      name: product.name,
      price: product.price,
      categoryId: categories.find(c => c.name === product.categoryName)?.id,
      tagId: tags.find(t => t.name === product.tagName)?.id,
      ingredientIds: product.ingredientsName
        ? ingredients.filter(i => product.ingredientsName.includes(i.name)).map(i => i.id)
        : [],
      imageUrl: product.imageUrl,
    });
    setIsEditOpen(true);
  };

  const openCreate = () => {
    setSelectedProduct(null);
    form.resetFields();
    setIsEditOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    const url = selectedProduct
      ? `${process.env.REACT_APP_API_URL}/products/${selectedProduct.id}`
      : `${process.env.REACT_APP_API_URL}/products`;
    const method = selectedProduct ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: values.name,
        price: values.price,
        categoryId: values.categoryId,
        tagId: values.tagId,
        ingredientsId: values.ingredientIds,
        imageUrl: values.imageUrl,
      }),
    });

    if (res.ok) {
      notification.success({ message: selectedProduct ? 'Товар обновлён' : 'Товар создан' });
      setIsEditOpen(false);
      loadData();
    } else {
      notification.error({ message: 'Ошибка при сохранении' });
    }
  };

  const handleDelete = async (id) => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/products/${id}`, { method: 'DELETE' });
    if (res.ok) {
      notification.success({ message: 'Товар удалён' });
      loadData();
    } else {
      notification.error({ message: 'Ошибка при удалении' });
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px', alignItems: 'center' }}>
        <h2>Товары</h2>
        <div style={{ display: 'flex', gap: '12px' }}>
          <Select
            placeholder="Фильтр по категории"
            allowClear
            style={{ width: '200px' }}
            value={filterCategory}
            onChange={(value) => setFilterCategory(value)}
          >
            {categories.map((c) => (
              <Select.Option key={c.id} value={c.id}>{c.name}</Select.Option>
            ))}
          </Select>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Добавить товар</Button>
        </div>
      </div>

      <Row gutter={[16, 16]}>
        {products
          .filter(p => !filterCategory || categories.find(c => c.name === p.categoryName)?.id === filterCategory)
          .map((product) => (
          <Col xs={24} sm={12} md={8} lg={6} key={product.id}>
            <Card
              hoverable

              style={{ height: '100%' }}
              cover={
                product.imageUrl ? (
                  <img
                    src={`${process.env.REACT_APP_API_URL}${product.imageUrl}`}
                    alt={product.name}
                    style={{ height: '220px', objectFit: 'contain' }}
                  />
                ) : (
                  <div style={{
                    height: '180px', background: '#f5f5f5',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    color: '#bbb', fontSize: '14px'
                  }}>

                  </div>
                )
              }
              onClick={() => openView(product)}
              actions={[
                <EditOutlined key="edit" onClick={(e) => { e.stopPropagation(); openEdit(product); }} />,
                <DeleteOutlined
                  key="delete"
                  style={{ color: 'red' }}
                  onClick={(e) => {
                    e.stopPropagation();
                    Modal.confirm({
                      title: 'Удалить товар?',
                      content: `Вы уверены, что хотите удалить "${product.name}"?`,
                      okText: 'Да', cancelText: 'Нет', centered: true,
                      onOk: () => handleDelete(product.id),
                    });
                  }}
                />,
              ]}
            >
              <Card.Meta
                title={product.name}
                description={
                  <>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#52c41a', marginBottom: '8px' }}>
                      {product.price} BYN
                    </div>
                    <Tag color="blue">{product.categoryName}</Tag>
                    <Tag color="orange">{product.tagName}</Tag>
                  </>
                }
              />
            </Card>
          </Col>
        ))}
      </Row>

      {/* Модалка просмотра */}
      <Modal
        title={selectedProduct?.name}
        open={isViewOpen}
        onCancel={() => setIsViewOpen(false)}
        footer={<Button onClick={() => setIsViewOpen(false)}>Закрыть</Button>}
        width={600}
      >
        {selectedProduct && (
          <div style={{ display: 'flex', gap: '24px' }}>
            <div style={{ flexShrink: 0 }}>
              {selectedProduct.imageUrl ? (
                <img
                  src={`${process.env.REACT_APP_API_URL}${selectedProduct.imageUrl}`}
                  alt={selectedProduct.name}
                  style={{ width: '200px', height: '200px', objectFit: 'contain', borderRadius: '8px' }}
                />
              ) : (
                <div style={{
                  width: '200px', height: '200px', background: '#f5f5f5',
                  borderRadius: '8px', display: 'flex', alignItems: 'center',
                  justifyContent: 'center', color: '#bbb', fontSize: '14px'
                }}>

                </div>
              )}
            </div>
            <Descriptions column={1} bordered size="middle" style={{ flex: 1 }}>
              <Descriptions.Item label="Название">{selectedProduct.name}</Descriptions.Item>
              <Descriptions.Item label="Цена">{selectedProduct.price} BYN</Descriptions.Item>
              <Descriptions.Item label="Категория">
                <Tag color="blue">{selectedProduct.categoryName}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Тег">
                <Tag color="orange">{selectedProduct.tagName}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Ингредиенты">
                {selectedProduct.ingredientsName?.length > 0
                  ? selectedProduct.ingredientsName.map((ing) => <Tag key={ing}>{ing}</Tag>)
                  : '—'}
              </Descriptions.Item>
            </Descriptions>
          </div>
        )}
      </Modal>

      {/* Модалка создания/редактирования */}
      <Modal
        title={selectedProduct ? 'Редактировать товар' : 'Новый товар'}
        open={isEditOpen}
        onCancel={() => setIsEditOpen(false)}
        onOk={handleSave}
        okText="Сохранить"
        cancelText="Отмена"
        width={500}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="Название" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="price" label="Цена" rules={[{ required: true }]}>
            <InputNumber min={0} step={0.1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="categoryId" label="Категория" rules={[{ required: true }]}>
            <Select>
              {categories.map((c) => (
                <Select.Option key={c.id} value={c.id}>{c.name}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="tagId" label="Тег" rules={[{ required: true }]}>
            <Select>
              {tags.map((t) => (
                <Select.Option key={t.id} value={t.id}>{t.name}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="ingredientIds" label="Ингредиенты">
            <Select mode="multiple" placeholder="Выберите ингредиенты">
              {ingredients.map((i) => (
                <Select.Option key={i.id} value={i.id}>{i.name}</Select.Option>
              ))}
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
                 const res = await fetch('${process.env.REACT_APP_API_URL}/api/images/upload', {
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
    </div>
  );
}