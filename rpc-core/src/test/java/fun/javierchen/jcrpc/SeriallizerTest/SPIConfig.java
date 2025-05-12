package fun.javierchen.jcrpc.SeriallizerTest;

import fun.javierchen.jcrpc.serializer.Serializer;
import org.junit.Test;

import java.util.ServiceLoader;

public class SPIConfig {

    /**
     * Java 内置的用于实现 SPI 的 API 接口
     */
    @Test
    public void loadService() {
        Serializer serializer = null;
        ServiceLoader<Serializer> services = ServiceLoader.load(Serializer.class);
        for (Serializer service: services){
            serializer = service;
        }

        System.out.println(serializer);
    }

}
