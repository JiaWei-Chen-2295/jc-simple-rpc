package fun.javierchen.jcrpc.fault.retry;

import fun.javierchen.jcrpc.spi.SPILoader;

public class RetryStrategyFactory {

    static {
        SPILoader.load(RetryStrategy.class);
    }


    public static RetryStrategy getInstance(String key) {
        return SPILoader.getInstance(RetryStrategy.class, key);
    }

}
