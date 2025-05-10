package fun.javierchen.jcrpc.server.tcp;

import fun.javierchen.jcrpc.server.HTTPServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HTTPServer {

    @Override
    public void start(int serverPort) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();

        netServer.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器并监听指定端口
        netServer.listen(serverPort, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port " + serverPort);
            } else {
                System.err.println("Failed to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpServer server = new VertxTcpServer();
        server.start(8886);
    }
}
