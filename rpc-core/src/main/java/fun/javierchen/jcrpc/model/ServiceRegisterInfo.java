package fun.javierchen.jcrpc.model;

public record ServiceRegisterInfo<T>(
        /*
         * 服务名称
         */
        String serviceName,
        /*
         * 服务实现类
         */
        Class<? extends T> serviceClassImpl
) {
}
