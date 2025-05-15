package fun.javierchen.jcrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import fun.javierchen.jcrpc.RpcApplication;
import fun.javierchen.jcrpc.model.RpcRequest;
import fun.javierchen.jcrpc.model.RpcResponse;
import fun.javierchen.jcrpc.model.ServiceMetaInfo;
import fun.javierchen.jcrpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 用于测试粘包半包
 */
public class VertxTcpClient {

    /**
     * 发送TCP协议
     *
     * @param selectedServiceMetaInfo
     * @param rpcRequest
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static RpcResponse doRequest(ServiceMetaInfo selectedServiceMetaInfo, RpcRequest rpcRequest) throws Exception {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> rpcResponseCompletableFuture = new CompletableFuture<>();
        netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(), result -> {
            if (result.succeeded()) {
                System.out.println("TCP client connected to server");
                NetSocket socket = result.result();
                // 发送数据
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerialization((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializerType()).getKey());
                header.setMessageType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);

                // 编码请求
                try {
                    Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                    socket.write(encodeBuffer);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误");
                }

                // 接收响应
                TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        rpcResponseCompletableFuture.complete(rpcResponseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误");
                    }
                });
                socket.handler(tcpBufferHandlerWrapper);
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
        RpcResponse rpcResponse = rpcResponseCompletableFuture.get();
        netClient.close();

        return rpcResponse;

    }

    public void start() throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8886, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Vertx listening port: 8886 Success !!");
                NetSocket socket = result.result();

                socket.write("Hello, server!Hello, server!Hello, server!Hello, server! I'm Client.");
                socket.handler(buffer -> {
                    System.out.println("Received: " + buffer.toString("UTF-8"));
                });
            } else {
                System.err.println("Connect failed: " + result.cause());
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        new VertxTcpClient().start();
    }
}
