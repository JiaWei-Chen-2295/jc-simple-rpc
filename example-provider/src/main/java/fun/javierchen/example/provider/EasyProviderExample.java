package fun.javierchen.example.provider;

import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.jcrpc.RpcApplication;
import fun.javierchen.jcrpc.config.RegistryConfig;
import fun.javierchen.jcrpc.config.RpcConfig;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import fun.javierchen.jcrpc.registry.LocalRegistry;
import fun.javierchen.jcrpc.registry.Registry;
import fun.javierchen.jcrpc.registry.RegistryFactory;
import fun.javierchen.jcrpc.server.HTTPServer;
import fun.javierchen.jcrpc.server.VertXHTTPServerImpl;
import fun.javierchen.jcrpc.server.tcp.VertxTcpServer;

import java.util.concurrent.ExecutionException;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {

        // 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = OrderService.class.getName();
        LocalRegistry.register(OrderServiceImpl.class, serviceName);

        // 注册服务到注册中心
        try {
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            System.out.println("注册出错");
        }

//        启动服务
//        HTTPServer httpServer = new VertXHTTPServerImpl();
//        httpServer.start(RpcApplication.getRpcConfig().getServerPort());

        // 启动新的 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }
}
