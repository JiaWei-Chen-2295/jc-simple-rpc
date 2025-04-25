package fun.javierchen.example.provider;

import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.simplerpc.RpcApplication;
import fun.javierchen.simplerpc.registry.LocalRegistry;
import fun.javierchen.simplerpc.server.HTTPServer;
import fun.javierchen.simplerpc.server.VertXHTTPServerImpl;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {

        // 框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(OrderServiceImpl.class, OrderService.class.getName());

        // 启动服务
        HTTPServer httpServer = new VertXHTTPServerImpl();
        httpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }
}
