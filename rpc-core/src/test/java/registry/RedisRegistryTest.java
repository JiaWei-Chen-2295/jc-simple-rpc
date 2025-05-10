package registry;

import fun.javierchen.jcrpc.config.RegistryConfig;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import fun.javierchen.jcrpc.registry.RedisRegistry;
import fun.javierchen.jcrpc.registry.RegistryFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RedisRegistryTest {

    private RedisRegistry registry;

    @Before
    public void init() {
        registry = (RedisRegistry) RegistryFactory.getInstance("redis");
        registry.init(RegistryConfig.builder().address("redis://127.0.0.1:6379").registry("redis").build());
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);

        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
    }

    @Test
    public void unRegistry() throws ExecutionException, InterruptedException {

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() throws Exception {
        register();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceKey);
        for (ServiceMetaInfo info : serviceMetaInfos) {
            System.out.println(info);
        }
    }

    @Test
    public void heartBeat() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        Thread.sleep(40 * 1000L);
    }

    @Test
    public void watch() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("service");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        Thread.sleep(50 * 1000L);
    }

}
