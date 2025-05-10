package fun.javierchen.jcrpc.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import fun.javierchen.jcrpc.config.RegistryConfig;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.ObjectListener;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

@Slf4j
public class RedisRegistry implements Registry {

    private RedissonClient redisson;

    private final String REDIS_ROOT_PATH = "/rpc/";

    /**
     * 记录当前注册的服务节点 key
     */
    private final Set<String> serviceNodeKeys = new ConcurrentHashSet<>();



    @Override
    public void init(RegistryConfig registryConfig) {
        String address = registryConfig.getAddress();
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address);
        // 使用其他的编码器 防止编码错误
        config.setCodec(new StringCodec());
        redisson = Redisson.create(config);
        log.info("RedisRegistry init success, address: {}", address);

        heartBeat();
        log.info("RedisRegistry heartBeat start");
        // 创建注册 ShutdownHook 用于资源的释放
        Runtime.getRuntime().addShutdownHook(new Thread(this::destory));
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        String serviceKey = serviceMetaInfo.getServiceNodeKey();
        String serviceNodeKey = REDIS_ROOT_PATH + serviceKey;
        String value = JSONUtil.toJsonStr(serviceMetaInfo);
        redisson.getBucket(serviceNodeKey).set(value, Duration.ofSeconds(30L));
        serviceNodeKeys.add(serviceNodeKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String key = REDIS_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        if (!serviceNodeKeys.contains(key)) {
            throw new RuntimeException("服务未注册");
        }
        boolean result = redisson.getBucket(key).delete();
        if (!result) {
            log.error("服务节点{}删除失败", key);
        }
        serviceNodeKeys.remove(key);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceNodeKey) {
        RKeys keys = redisson.getKeys();
        String prefix = REDIS_ROOT_PATH + serviceNodeKey + '*';
        // 获取匹配的键名迭代器
        Iterable<String> matchedKeys = keys.getKeysByPattern(prefix);
        if (matchedKeys == null) {
            return Collections.emptyList();
        }
        Iterator<String> iterator = StreamSupport.stream(matchedKeys.spliterator(), false).iterator();
        List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
        while(iterator.hasNext()) {
            String key = iterator.next();
            String valueStr = redisson.getBucket(key).get().toString();
            ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(valueStr, ServiceMetaInfo.class);
            serviceMetaInfoList.add(serviceMetaInfo);
            // 被发现的服务进行监听
//            watch(key, serviceNodeKey);
        }
        return serviceMetaInfoList;
    }

    @Override
    public void destory() {
        for (String serviceNodeKey : serviceNodeKeys) {
            boolean result = redisson.getBucket(serviceNodeKey).delete();
            if (!result) {
                log.error("服务节点{}删除失败", serviceNodeKey);
            }
        }

        if (redisson != null) {
            redisson.shutdown();
        }

    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String serviceNodeKey : serviceNodeKeys) {
                try {
                    RBucket<String> bucket = redisson.getBucket(serviceNodeKey);
                    String value = bucket.get();
                    if (value == null) {
                        continue;
                    }
                    bucket.set(value, Duration.ofSeconds(30L));
                } catch (Exception e) {
                    throw new RuntimeException("心跳失败" + serviceNodeKey, e);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey, String serviceKey) {
        // 因为没有实现缓存 所以无需 watch 键值的变化
    }
}
