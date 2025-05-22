package fun.javierchen.jcrpc.bootstrap;

import fun.javierchen.jcrpc.RpcApplication;
import fun.javierchen.jcrpc.config.RegistryConfig;
import fun.javierchen.jcrpc.config.RpcConfig;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import fun.javierchen.jcrpc.model.ServiceRegisterInfo;
import fun.javierchen.jcrpc.registry.LocalRegistry;
import fun.javierchen.jcrpc.registry.Registry;
import fun.javierchen.jcrpc.registry.RegistryFactory;
import fun.javierchen.jcrpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {

        // 框架初始化
        RpcApplication.init();
        // 获取配置信息
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 批量注册服务到服务中心
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.serviceName();
            LocalRegistry.register(serviceRegisterInfo.serviceClassImpl(), serviceName);

            // 注册服务到注册中心
            try {
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


        }

        // 启动新的 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }

}
