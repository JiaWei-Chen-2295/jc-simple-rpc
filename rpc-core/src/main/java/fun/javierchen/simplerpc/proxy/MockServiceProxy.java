package fun.javierchen.simplerpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理(JDK 动态代理实现)
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultValue(returnType);
    }

    public Object getDefaultValue(Class<?> typeClass) {
        if (typeClass.isPrimitive()) {
            if (typeClass == int.class) {
                return 0;
            } else if (typeClass == short.class) {
                return (short) 0;
            } else if (typeClass == char.class) {
                return '0';
            } else if (typeClass == long.class) {
                return 0L;
            } else if (typeClass == boolean.class) {
                return false;
            }
        }

        try {
            log.info("创建{}类型的mock类成功", typeClass.getName());
            return typeClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Failed to create instance of {}", typeClass.getName(), e);
            return null;
        }
    }
}
