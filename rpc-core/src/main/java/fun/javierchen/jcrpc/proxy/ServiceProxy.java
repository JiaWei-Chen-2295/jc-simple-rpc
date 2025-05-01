package fun.javierchen.jcrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import fun.javierchen.jcrpc.RpcApplication;
import fun.javierchen.jcrpc.config.RpcConfig;
import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.serializer.Serializer;
import fun.javierchen.jcrpc.serializer.SerializerFactory;
import fun.javierchen.jcrpc.serializer.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 使用 JDK 动态代理对需要的服务进行代理
 * 作用：
 * 让使用者调用方法时 就像调用已有的服务一样无感
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializerType());
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypeList(method.getParameterTypes())
                .args(args)
                .build();

        // 序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);

        // 发送请求
        try (HttpResponse httpResponse = HttpRequest.post(String.format("http://%s:%d", RpcApplication.getRpcConfig().getServerHost(), RpcApplication.getRpcConfig().getServerPort()))
                .body(bodyBytes)
                .execute()
        ) {
            byte[] resultBytes = httpResponse.bodyBytes();

            // 反序列话获取响应对象
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return rpcResponse.getData();
        } catch (Exception e) {
            log.error("发送调用请求失败{}", e.getLocalizedMessage());
        }
        return null;
    }
}
