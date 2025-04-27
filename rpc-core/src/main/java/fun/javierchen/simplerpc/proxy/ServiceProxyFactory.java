package fun.javierchen.simplerpc.proxy;

import fun.javierchen.simplerpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 动态代理工厂 根据指定类创建动态代理对象
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass) {

        if (RpcApplication.getRpcConfig().isUseMock()) {
            return getMockProxy(serviceClass);
        }

        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

    /**
     * 根据服务类获取 Mock 代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[] {serviceClass},
                new MockServiceProxy()
        );
    }
}
