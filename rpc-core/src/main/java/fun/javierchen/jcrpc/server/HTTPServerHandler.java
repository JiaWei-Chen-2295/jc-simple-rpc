package fun.javierchen.jcrpc.server;

import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.registry.LocalRegistry;
import fun.javierchen.jcrpc.serializer.Serializer;
import fun.javierchen.jcrpc.serializer.impl.JdkSerializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理
 */

public class HTTPServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = new JdkSerializer();

        // 记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 异步处理 HTTP 请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
            // 构造响应
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为 null，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取需要调用的服务类
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Object implInstance = implClass.getDeclaredConstructor().newInstance();
                Method implClassMethod = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypeList());
                Object result = implClassMethod.invoke(implInstance, rpcRequest.getArgs());
                // 封装返回对象
                rpcResponse.setData(result);
                rpcResponse.setResponseType(implClassMethod.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(request, rpcResponse, serializer);

        });

    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }

}
