package fun.javierchen.jcrpc.server.tcp;

import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.protocol.ProtocolMessage;
import fun.javierchen.jcrpc.protocol.ProtocolMessageDecoder;
import fun.javierchen.jcrpc.protocol.ProtocolMessageEncoder;
import fun.javierchen.jcrpc.protocol.ProtocolMessageTypeEnum;
import fun.javierchen.jcrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP服务端处理器
 * 接收请求，反射调用服务类
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {

        // 处理连接
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接收请求
            ProtocolMessage<RpcRequest> protocolMessage;

            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RpcRequest rpcRequest = protocolMessage.getBody();


            // 处理请求
            // 并构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            try {
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypeList());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setResponseType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 发送响应 并编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setMessageType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });

        netSocket.handler(bufferHandlerWrapper);
    }
}
