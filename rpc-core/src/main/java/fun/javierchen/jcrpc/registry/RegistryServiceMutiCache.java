package fun.javierchen.jcrpc.registry;

import fun.javierchen.jcrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryServiceMutiCache {

    /**
     * 服务缓存Map
     */
    private final Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     */
    public void writeCache(String serviceKey, List<ServiceMetaInfo> infoList) {
        serviceCache.put(serviceKey, infoList);
    }

    /**
     * 读缓存
     */
    public List<ServiceMetaInfo> readCache(String serviceKey) {
        return serviceCache.get(serviceKey);
    }

    /**
     * 删除缓存
     * @param serviceKey
     */
    public void removeCache(String serviceKey) {
        serviceCache.remove(serviceKey);
    }


}
