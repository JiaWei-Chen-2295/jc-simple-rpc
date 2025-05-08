package fun.javierchen.example.consumer;

import fun.javierchen.example.common.model.Order;
import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.jcrpc.proxy.ServiceProxyFactory;

public class ConsumerWithRegisterCacheExample {
    public static void main(String[] args) {
        // 获取远程的订单服务
        // 第一次访问订单服务，会从注册中心获取服务信息，并缓存
        System.out.println("第一次访问订单服务，会从注册中心获取服务信息，并缓存");
        OrderService orderService1 = ServiceProxyFactory.getProxy(OrderService.class);
        Order order1 = orderService1.getOrder(329L);

        OrderService orderService2 = ServiceProxyFactory.getProxy(OrderService.class);
        Order order2 = orderService2.getOrder(329L);

        OrderService orderService3 = ServiceProxyFactory.getProxy(OrderService.class);
        Order order3 = orderService3.getOrder(329L);
    }
}
