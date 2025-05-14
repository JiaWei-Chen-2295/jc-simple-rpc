package fun.javierchen.jcrpc.loadbalancer;

import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * 轮询负载均衡
 */
@Slf4j
public class RoundRobinLoadBalancer implements LoadBalancer{
    /**
     * 当前轮询的下标
     */
    private final static AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // 只有一个服务 就不需要轮询
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 通过取模来进行算法轮询
        int index = currentIndex.getAndIncrement() % size;
        log.info("当前使用的是序号{}", index);
        return serviceMetaInfoList.get(index);
    }
}
