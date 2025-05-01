package fun.javierchen.example.consumer;

import fun.javierchen.example.common.model.Order;
import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.jcrpc.proxy.ServiceProxyFactory;

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
            short num = orderService.getNum();
            System.out.println("Mock 后接口的返回值是" + num);
            Object obj = orderService.getObj();
            System.out.println("Mock 对象的接口" + obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
