package fun.javierchen.jcrpc.loadbalancer;

import fun.javierchen.jcrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {
    /**
     * 一致性哈希环 用于存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();
    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 每次调用 select 都会构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = getHash(requestParams);
        // 选择最接近且大于等于调用请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> integerServiceMetaInfoEntry = virtualNodes.ceilingEntry(hash);
        if (integerServiceMetaInfoEntry == null) {
            // 如果没有虚拟节点大于调用请求 hash 值，则选择环中的第一个节点
            integerServiceMetaInfoEntry = virtualNodes.firstEntry();
        }

        return integerServiceMetaInfoEntry.getValue();
    }

    private int getHash(Object key) {
        return key.hashCode();
    }
}
