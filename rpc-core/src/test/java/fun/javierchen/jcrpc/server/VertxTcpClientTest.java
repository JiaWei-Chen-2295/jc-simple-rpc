package fun.javierchen.jcrpc.server;

import io.vertx.core.Vertx;

public class VertxTcpClientTest {

    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8886, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();
                for (int i = 0; i < 2000; i++) {
                    // 发送数据
                    socket.write("Hello, server!Hello, server!Hello, server!");
                }
                // 接收响应
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpClientTest client = new VertxTcpClientTest();
        client.start();
    }

}
