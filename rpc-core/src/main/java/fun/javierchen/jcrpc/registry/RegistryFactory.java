package fun.javierchen.jcrpc.registry;

import fun.javierchen.jcrpc.spi.SPILoader;

/**
 * 注册中心工厂
 */
public class RegistryFactory {
    static {
        SPILoader.load(Registry.class);
    }

    /**
     * 加载默认的注册中心 - etcd
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getInstance(String key) {
        return SPILoader.getInstance(Registry.class, key);
    }

}
