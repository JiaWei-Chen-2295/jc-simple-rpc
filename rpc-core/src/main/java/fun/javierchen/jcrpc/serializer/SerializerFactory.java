package fun.javierchen.jcrpc.serializer;

import fun.javierchen.jcrpc.serializer.impl.JdkSerializer;
import fun.javierchen.jcrpc.spi.SPILoader;

public class SerializerFactory {

    static {
        SPILoader.load(Serializer.class);
    }


    /**
     * 默认的序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SPILoader.getInstance(Serializer.class, key);
    }

}

