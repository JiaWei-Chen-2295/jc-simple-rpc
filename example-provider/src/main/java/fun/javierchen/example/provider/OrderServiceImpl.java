package fun.javierchen.example.provider;

import fun.javierchen.example.common.model.Order;
import fun.javierchen.example.common.service.OrderService;

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
