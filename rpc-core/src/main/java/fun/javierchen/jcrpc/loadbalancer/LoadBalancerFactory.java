package fun.javierchen.jcrpc.loadbalancer;

import fun.javierchen.jcrpc.spi.SPILoader;

public class LoadBalancerFactory {
    static {
        SPILoader.load(LoadBalancer.class);
    }

    private static final LoadBalancer DEFAULT_LOADER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key) {
        return SPILoader.getInstance(LoadBalancer.class, key);
    }
}
