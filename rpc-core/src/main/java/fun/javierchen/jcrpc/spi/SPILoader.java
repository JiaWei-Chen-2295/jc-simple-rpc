package fun.javierchen.jcrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import fun.javierchen.jcrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SPILoader {
    /**
     * 存储已经加载的类 接口名 key -> 实现类
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 对象实例缓存 类路径=> 对象实例
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system";
    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/system";
    /**
     * 需要扫描的路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};
    /**
     * 动态加载的类型表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    public static void loadAll() {
        log.info("加载所有的 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 加载某个类型
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为{}的SPI", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            // todo: 如果作为框架引入 无法获得真实路径
            List<URL> resources = ResourceUtil.getResources(scanDir + File.separator +  loadClass.getName());

            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }

                } catch (Exception e) {
                    log.error("spi 资源加载失败{}", e.getLocalizedMessage());
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 获取某个接口的实例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SPI 加载 %s 类型失败", tClassName));
        }

        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }

        //缓存的实例没有命中 就加载这个实例 并添加到缓存
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }
}
