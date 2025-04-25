package fun.javierchen.example.consumer;

import fun.javierchen.example.common.model.Order;
import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.simplerpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {

        // 获取远程的订单服务
        OrderService orderService = ServiceProxyFactory.getProxy(OrderService.class);

        long orderId = 329L;
        try {
            Order order = orderService.getOrder(orderId);
            if (order != null) {
                System.out.println("订单的价格是: " + order.getPrice());
            }  else {
                System.out.println("订单错误");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
