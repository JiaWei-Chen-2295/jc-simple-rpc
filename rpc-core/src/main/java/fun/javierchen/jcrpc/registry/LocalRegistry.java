package fun.javierchen.jcrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 * 用于映射请求和 service
 */
public class LocalRegistry {

    /**
     * 注册信息
     */
    private static final Map<String, Class<?>> registryMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param clazz
     * @param serviceName
     */
    public static void register(Class<?> clazz, String serviceName) {
        registryMap.put(serviceName, clazz);
    }

    /**
     * 获取服务
     */
    public static Class<?> get(String serviceName) {
        return registryMap.get(serviceName);
    }

    /**
     * 删除服务
     */
    public static void remove(String serviceName) {
        registryMap.remove(serviceName);
    }
}
