package fun.javierchen.example.common.service;

import fun.javierchen.example.common.model.Order;
import java.math.BigDecimal;


public class OrderServiceImpl implements OrderService {
    @Override
    public Order getOrder(long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setPrice(new BigDecimal("55.25"));
        return order;
    }
}
