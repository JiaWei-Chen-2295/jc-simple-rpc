package fun.javierchen.simplerpc;

import fun.javierchen.simplerpc.config.RpcConfig;
import fun.javierchen.simplerpc.constant.RpcConstant;
import fun.javierchen.simplerpc.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("config为{}", newRpcConfig);
    }

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.CONFIG_PREFIX);
        } catch (Exception e) {
            log.error("获取文本配置失败{}", e.getLocalizedMessage());
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 双检锁单例模式
     * 确保配置是单例的
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}