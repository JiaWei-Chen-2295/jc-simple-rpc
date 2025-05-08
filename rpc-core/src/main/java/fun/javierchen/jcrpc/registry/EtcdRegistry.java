package fun.javierchen.jcrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import fun.javierchen.jcrpc.config.RegistryConfig;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {
    /**
     * 本机注册节点集合 用于续期
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 节点信息缓存
     */
    private final RegistryServiceMutiCache registryServiceMutiCache = new RegistryServiceMutiCache();

    /**
     * 监听的 key 集合 防止重复监听
     */
    private final Set<String> watchedKeySet = new ConcurrentHashSet<>();

    private Client client;
    private KV kvClient;

    public static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        // 开启心跳机制
        heartBeat();
        // 创建注册 ShutdownHook 用于资源的释放
        Runtime.getRuntime().addShutdownHook(new Thread(this::destory));
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        // 添加节点到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        // 服务注销时 移除缓存的节点
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存中读取服务
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceMutiCache.readCache(serviceKey);
        if (CollUtil.isNotEmpty(serviceMetaInfoList)) {
            log.info("缓存命中，key:{}", serviceKey);
            return serviceMetaInfoList;
        }

        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> fetchedServiceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        // 添加对这个 key 的监听
                        watch(key, serviceMetaInfo.getServiceKey());
                        return serviceMetaInfo;
                    })
                    .collect(Collectors.toList());
            // 进行缓存
            log.info("缓存未命中，正在添加中，key:{}", serviceKey);
            registryServiceMutiCache.writeCache(serviceKey, fetchedServiceMetaInfoList);
            return fetchedServiceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destory() {
        log.info("当前节点下线");

        // 删除所有服务节点
        for (String registerKey : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException("节点" + registerKey + "下线失败", e);
            }
        }

        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 10 秒续签一次服务
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String registerKey : localRegisterNodeKeySet) {
                try {
                    // 获取到注册中心存储的键值
                    List<KeyValue> kvs = kvClient
                            .get(ByteSequence.from(registerKey, StandardCharsets.UTF_8))
                            .get().getKvs();
                    // 检查键值是否过期/存在
                    if (CollUtil.isEmpty(kvs)) {
                        continue;
                    }
                    // 如果存在 就续签 (重新注册)
                    KeyValue keyValue = kvs.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException("续签失败" + registerKey, e);
                }
            }

        });
        // 启动定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey, String serviceKey) {
        log.info("开始监听服务节点:{}", serviceNodeKey);
        // 获取 watch 操作客户端对象
        Watch watchClient = client.getWatchClient();
        // 加入到已经监听的集合
        if (!watchedKeySet.add(serviceNodeKey)) {
            // 代表已经监听过
            return;
        }
        // 监听当前服务节点
        watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8),
                WatchOption.builder().isPrefix(true).build(),
                watchResponse -> {
                    // 监听更新和删除事件
                    for (WatchEvent event : watchResponse.getEvents()) {
                        switch (event.getEventType()) {
                            // 删除时清空缓存
                            case DELETE:
                                registryServiceMutiCache.removeCache(serviceKey);
                                break;
                            case PUT:
                                registryServiceMutiCache.removeCache(serviceKey);
                                break;
                            default:
                                break;
                        }
                    }
                }
        );


    }
}