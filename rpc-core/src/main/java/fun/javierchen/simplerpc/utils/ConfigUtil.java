package fun.javierchen.simplerpc.utils;

import cn.hutool.setting.dialect.Props;
import org.apache.commons.lang.StringUtils;

public class ConfigUtil {
    private ConfigUtil() {
    }

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }


    /**
     * 加载对象 支持区分环境
     *
     * @param tClass      配置类型
     * @param prefix
     * @param environment 环境
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder stringBuilder = new StringBuilder("application");
        if (StringUtils.isNotBlank(environment)) {
            stringBuilder.append("-").append(environment);
        }
        stringBuilder.append(".properties");
        String configFileName = stringBuilder.toString();
        Props props = new Props(configFileName);
        return props.toBean(tClass, prefix);
    }
}
