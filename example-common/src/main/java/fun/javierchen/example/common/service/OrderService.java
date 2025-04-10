package fun.javierchen.example.common.service;

import fun.javierchen.example.common.model.Order;

public interface OrderService {
    /**
     * 获取订单信息
     * @return
     */
    Order getOrder(long orderId);
}
