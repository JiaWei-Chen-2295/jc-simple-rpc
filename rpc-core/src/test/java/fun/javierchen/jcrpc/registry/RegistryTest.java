package fun.javierchen.jcrpc.registry;

import fun.javierchen.jcrpc.config.RegistryConfig;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import fun.javierchen.jcrpc.registry.EtcdRegistry;
import fun.javierchen.jcrpc.registry.Registry;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void registry() throws ExecutionException, InterruptedException {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);

        registry.register(serviceMetaInfo);

        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1236);

        registry.register(serviceMetaInfo);

        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);

        registry.register(serviceMetaInfo);

    }

    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        System.out.println(serviceMetaInfoList);
    }

    @Test
    public void unRegistry() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void heartBeat() throws Exception {
        // init 方法中已经执行心跳检测了
        // 键值默认 30 s 过期
        // 当 TTL 剩余 20 S 的时候就会自动续期 到了 30 秒
        registry();
        // 阻塞 1 分钟
        Thread.sleep(60 * 1000L);
    }
}
