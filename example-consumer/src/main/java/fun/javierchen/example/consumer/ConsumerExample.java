package fun.javierchen.example.consumer;

import fun.javierchen.example.common.model.Order;
import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.jcrpc.bootstrap.ConsumerBootstrap;
import fun.javierchen.jcrpc.proxy.ServiceProxyFactory;

import java.math.BigDecimal;

public class ConsumerExample {
    public static void main(String[] args) {
        ConsumerBootstrap.init();


        try {
            // 获取代理
            OrderService proxyOrderService = ServiceProxyFactory.getProxy(OrderService.class);
            Order order = proxyOrderService.getOrder(329L);
            BigDecimal price = order.getPrice();
            System.out.println("订单的价格是" + price);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
