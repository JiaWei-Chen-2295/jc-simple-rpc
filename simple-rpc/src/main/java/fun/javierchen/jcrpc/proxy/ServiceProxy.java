package fun.javierchen.jcrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.serializer.Serializer;
import fun.javierchen.jcrpc.serializer.impl.JdkSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 使用 JDK 动态代理对需要的服务进行代理
 * 作用：
 * 让使用者调用方法时 就像调用已有的服务一样无感
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();
        String name = method.getName();
        System.out.println("请求类的方法名字" + name);
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
        // warning !! 这里被硬编码了
        try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                .body(bodyBytes)
                .execute()
        ) {
            byte[] resultBytes = httpResponse.bodyBytes();

            // 反序列话获取响应对象
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
