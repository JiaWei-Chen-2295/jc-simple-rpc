package fun.javierchen.jcrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import fun.javierchen.jcrpc.RpcApplication;
import fun.javierchen.jcrpc.config.RpcConfig;
import fun.javierchen.jcrpc.constant.RpcConstant;
import fun.javierchen.jcrpc.fault.retry.RetryStrategy;
import fun.javierchen.jcrpc.fault.retry.RetryStrategyFactory;
import fun.javierchen.jcrpc.loadbalancer.LoadBalancer;
import fun.javierchen.jcrpc.loadbalancer.LoadBalancerFactory;
import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import fun.javierchen.jcrpc.protocol.*;
import fun.javierchen.jcrpc.registry.Registry;
import fun.javierchen.jcrpc.registry.RegistryFactory;
import fun.javierchen.jcrpc.serializer.Serializer;
import fun.javierchen.jcrpc.serializer.SerializerFactory;
import fun.javierchen.jcrpc.serializer.impl.JdkSerializer;
import fun.javierchen.jcrpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂时没有服务");
        }

        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getServiceName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        // 发送 TCP 请求 使用重试机制
        RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
        RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                VertxTcpClient.doRequest(selectedServiceMetaInfo, rpcRequest)
        );
        return rpcResponse.getData();

    }
}
