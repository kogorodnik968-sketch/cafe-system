import { useState } from 'react';
import { Layout, Menu, Card, Row, Col, Badge, Button, Modal, List } from 'antd';
import {
  CoffeeOutlined,
  SettingOutlined,
  ShoppingCartOutlined,
   UserOutlined,
  LogoutOutlined,
  PlusOutlined,
  MinusOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import ProductManage from './components/ProductManage';
import CategoryManage from './components/CategoryManage';
import TagManage from './components/TagManage';
import IngredientManage from './components/IngredientManage';
import CustomerManage from './components/CustomerManage';
import EmployeeManage from './components/EmployeeManage';
import OrderManage from './components/OrderManage';
import LoginPage from './components/LoginPage';
import ProfilePage from './components/ProfilePage';

const { Header, Sider, Content } = Layout;

function App() {
  const [user, setUser] = useState(null);
  const [view, setView] = useState('menu');
  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [, setSelectedCategory] = useState(null);
  const [cart, setCart] = useState([]);
  const [isCartOpen, setIsCartOpen] = useState(false);

  const cartTotal = cart.reduce((sum, item) => sum + item.quantity, 0);
  const cartSum = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const loadCategories = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/category`);
    const data = await res.json();
    setCategories(data);
    if (data.length > 0) loadProducts(data[0].id);
  };

  const loadProducts = async (categoryId) => {
    const url = categoryId
      ? `${process.env.REACT_APP_API_URL}/products/by-category/${categoryId}`
      : `${process.env.REACT_APP_API_URL}/products`;
    const res = await fetch(url);
    const data = await res.json();
    setProducts(data);
    setSelectedCategory(categoryId);
  };

  const addToCart = (product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.productId === product.id);
      if (existing) return prev.map((item) =>
          item.productId === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      return [...prev, { productId: product.id, name: product.name, price: product.price, quantity: 1 }];
    });
  };

  const updateQuantity = (productId, delta) => {
    setCart((prev) =>
      prev.map((item) =>
          item.productId === productId ? { ...item, quantity: item.quantity + delta } : item
        ).filter((item) => item.quantity > 0)
    );
  };

  const removeFromCart = (productId) => {
    setCart((prev) => prev.filter((item) => item.productId !== productId));
  };

  const submitOrder = async () => {
    const res = await fetch(`${process.env.REACT_APP_API_URL}/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        customerId: user.role === 'CUSTOMER' ? user.id : 10,
        items: cart.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
      }),
    });
    if (res.ok) {
      alert('Заказ оформлен!');
      setCart([]);
      setIsCartOpen(false);
    } else {
      alert('Ошибка при оформлении заказа');
    }
  };

  const getCartItem = (productId) => cart.find((item) => item.productId === productId);

  if (!user) {
    return <LoginPage onLogin={(u) => { setUser(u); setView('menu'); }} />;
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ color: 'white', fontSize: '20px', fontWeight: 'bold' }}>☕ Café</div>
        <Badge count={cartTotal} offset={[-5, 5]}>
          <Button
            type="text"
            icon={<ShoppingCartOutlined style={{ color: 'white', fontSize: '22px' }} />}
            onClick={() => setIsCartOpen(true)}
          />
        </Badge>
      </Header>

      <Layout>
        <Sider width={220} style={{ background: '#fff' }}>
          <Menu mode="inline" defaultOpenKeys={[]} selectedKeys={view === 'menu' ? ['menu'] : [view]} style={{ height: '100%', borderRight: 0 }}>
            <Menu.SubMenu key="menu" icon={<CoffeeOutlined />} title="Меню" onTitleClick={() => { setView('menu'); if (categories.length === 0) loadCategories(); }}>
              {categories.map((cat) => (
                <Menu.Item key={`cat-${cat.id}`} icon={<CoffeeOutlined />} onClick={() => { setView('menu'); loadProducts(cat.id); }}>
                  {cat.name}
                </Menu.Item>
              ))}
            </Menu.SubMenu>
            <Menu.Divider />
            {user && user.role !== 'CUSTOMER' && (
              <Menu.SubMenu key="admin" icon={<SettingOutlined />} title="Управление" onTitleClick={() => setView('admin')}>
                <Menu.Item key="admin-products" onClick={() => setView('admin-products')}>Товары</Menu.Item>
                <Menu.Item key="admin-categories" onClick={() => setView('admin-categories')}>Категории</Menu.Item>
                <Menu.Item key="admin-tags" onClick={() => setView('admin-tags')}>Теги</Menu.Item>
                <Menu.Item key="admin-ingredients" onClick={() => setView('admin-ingredients')}>Ингредиенты</Menu.Item>
                <Menu.Item key="admin-customers" onClick={() => setView('admin-customers')}>Клиенты</Menu.Item>
                <Menu.Item key="admin-employees" onClick={() => setView('admin-employees')}>Сотрудники</Menu.Item>
                <Menu.Item key="admin-orders" onClick={() => setView('admin-orders')}>Заказы</Menu.Item>

              </Menu.SubMenu>
            )}
            <Menu.Divider />
           <Menu.Item key="profile" icon={<UserOutlined />} onClick={() => setView('profile')}>Профиль</Menu.Item>
           <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={() => {
             setUser(null);
             setView('menu');
             setCategories([]);
             setProducts([]);
             setCart([]);
           }}>Выйти</Menu.Item>
          </Menu>
        </Sider>

        <Content style={{ padding: '24px' }}>
          {view === 'menu' && (
            <>
              <h2>Меню</h2>
              <Row gutter={[16, 16]}>
                {products.map((product) => {
                  const cartItem = getCartItem(product.id);
                  return (
                    <Col xs={24} sm={12} md={8} lg={6} key={product.id}>
                      <Card
                        hoverable
                        cover={
                          product.imageUrl ? (
                            <img
                              src={`${process.env.REACT_APP_API_URL}${product.imageUrl}`}
                              alt={product.name}
                              style={{ height: '220px', objectFit: 'contain' }}
                            />
                          ) : (
                            <div style={{
                              height: '220px', background: '#f5f5f5',
                              display: 'flex', alignItems: 'center', justifyContent: 'center',
                              color: '#bbb', fontSize: '14px'
                            }}>
                              🖼️ Фото
                            </div>
                          )
                        }
                      >
                        <Card.Meta
                          title={product.name}
                          description={
                            <>
                              <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#52c41a', marginBottom: '8px' }}>
                                {product.price} BYN
                              </div>
                              <p>Категория: {product.categoryName}</p>
                              <p>Тег: {product.tagName}</p>
                            </>
                          }
                        />
                        <div style={{ marginTop: '12px' }}>
                          {cartItem ? (
                            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
                              <Button size="small" icon={<MinusOutlined />} onClick={() => updateQuantity(product.id, -1)} />
                              <span style={{ fontSize: '16px', fontWeight: 'bold', minWidth: '24px', textAlign: 'center' }}>{cartItem.quantity}</span>
                              <Button size="small" icon={<PlusOutlined />} onClick={() => updateQuantity(product.id, 1)} />
                            </div>
                          ) : (
                            <Button type="primary" icon={<ShoppingCartOutlined />} block onClick={() => addToCart(product)}>В корзину</Button>
                          )}
                        </div>
                      </Card>
                    </Col>
                  );
                })}
              </Row>
            </>
          )}

          {view === 'admin' && <div><h2>Панель управления</h2><p>Выберите раздел в боковом меню.</p></div>}
          {view === 'admin-products' && <ProductManage />}
          {view === 'admin-categories' && <CategoryManage />}
          {view === 'admin-tags' && <TagManage />}
          {view === 'admin-ingredients' && <IngredientManage />}
          {view === 'admin-customers' && <CustomerManage />}
          {view === 'admin-employees' && <EmployeeManage />}
          {view === 'admin-orders' && <OrderManage />}
          {view === 'profile' && <ProfilePage user={user} />}
        </Content>
      </Layout>

      <Modal
        title="Корзина"
        open={isCartOpen}
        onCancel={() => setIsCartOpen(false)}
        footer={[
          <Button key="cancel" onClick={() => setIsCartOpen(false)}>Закрыть</Button>,
          <Button key="submit" type="primary" disabled={cart.length === 0} onClick={submitOrder}>Оформить заказ</Button>,
        ]}
      >
        {cart.length === 0 ? (
          <p>Корзина пуста</p>
        ) : (
          <List
            dataSource={cart}
            renderItem={(item) => (
              <List.Item
                actions={[
                  <Button size="small" icon={<MinusOutlined />} onClick={() => updateQuantity(item.productId, -1)} />,
                  <span style={{ minWidth: '24px', textAlign: 'center', fontWeight: 'bold' }}>{item.quantity}</span>,
                  <Button size="small" icon={<PlusOutlined />} onClick={() => updateQuantity(item.productId, 1)} />,
                  <Button size="small" danger icon={<DeleteOutlined />} onClick={() => removeFromCart(item.productId)} />,
                ]}
              >
                <List.Item.Meta
                  title={item.name}
                  description={`${item.price} BYN × ${item.quantity} = ${(item.price * item.quantity).toFixed(2)} BYN`}
                />
              </List.Item>
            )}
          />
        )}
        {cart.length > 0 && (
          <div style={{ textAlign: 'right', marginTop: '16px', fontSize: '18px', fontWeight: 'bold' }}>
            Итого: {cartSum.toFixed(2)} BYN
          </div>
        )}
      </Modal>
    </Layout>
  );
}

export default App;