import { useState, useEffect } from 'react';
import { Table, Tag, Modal, Descriptions, Button} from 'antd';

const statusColors = {
  ACCEPTED: 'blue',
  PREPARING: 'orange',
  READY: 'green',
  DELIVERED: 'purple',
  CANCELLED: 'red',
};

const statusLabels = {
  ACCEPTED: 'Принят',
  PREPARING: 'Готовится',
  READY: 'Готов',
  DELIVERED: 'Выдан',
  CANCELLED: 'Отменён',
};

export default function OrderManage() {
  const [orders, setOrders] = useState([]);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadData = async () => {
    setLoading(true);
    const res = await fetch(`${process.env.REACT_APP_API_URL}/orders`);
    setOrders(await res.json());
    setLoading(false);
  };

  useEffect(() => { loadData(); }, []);

  const openView = (order) => {
    setSelectedOrder(order);
    setIsViewOpen(true);
  };

  const columns = [
    {
      title: 'Статус',
      dataIndex: 'status',
      key: 'status',
      render: (status) => <Tag color={statusColors[status] || 'default'}>{statusLabels[status] || status}</Tag>,
    },
    {
      title: 'Сумма',
      dataIndex: 'totalPrice',
      key: 'totalPrice',
      render: (v) => `${v} BYN`,
    },
{
    title: 'Клиент',
    key: 'customer',
    render: (_, record) => record.customerName || record.customerFirstName
      ? `${record.customerLastName || ''} ${record.customerFirstName || ''}`.trim() || record.customerName
      : '—',
  }
  ];

  return (
    <div>
      <h2 style={{ marginBottom: '24px' }}>Заказы</h2>

      <Table
        dataSource={orders}
        columns={columns}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
        onRow={(record) => ({
          onClick: () => openView(record),
          style: { cursor: 'pointer' },
        })}
      />

      <Modal
        title={`Заказ №${selectedOrder?.id}`}
        open={isViewOpen}
        onCancel={() => setIsViewOpen(false)}
        footer={<Button onClick={() => setIsViewOpen(false)}>Закрыть</Button>}
        width={800}
      >
        {selectedOrder && (
          <>
            <Descriptions column={2} bordered size="middle" style={{ marginBottom: '20px' }}>
              <Descriptions.Item label="Клиент">
                  {selectedOrder.customerLastName} {selectedOrder.customerFirstName}
                </Descriptions.Item>
              <Descriptions.Item label="Статус">
                <Tag color={statusColors[selectedOrder.status]}>
                  {statusLabels[selectedOrder.status] || selectedOrder.status}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Сумма">{selectedOrder.totalPrice} BYN</Descriptions.Item>
            </Descriptions>

            <h4>Товары:</h4>
            <Table
              dataSource={selectedOrder.orderItems || selectedOrder.items || []}
              rowKey="id"
              pagination={false}
              size="small"
              columns={[
                { title: 'Товар', dataIndex: 'productName', key: 'productName' },
                { title: 'Кол-во', dataIndex: 'quantity', key: 'quantity' },
                { title: 'Цена', dataIndex: 'price', key: 'price', render: (v) => `${v} BYN` },
              ]}
            />
          </>
        )}
      </Modal>
    </div>
  );
}