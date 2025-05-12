package fun.javierchen.jcrpc.server;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import org.junit.Before;
import org.junit.Test;

public class VertxTcpServerTest {

    public void start(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(handler -> {
            handler.handler(buffer -> {
                String testMessage = "Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!Hello, server!";
                int messageLength = testMessage.getBytes().length;

                // 解决半包
                // 判断接收的消息是否符合预期 符合才接收
                if (buffer == null || buffer.length() == 0) {
                    throw new RuntimeException("消息 buffer 为空");
                }
                if (buffer.getBytes().length < testMessage.length()) {
                    throw new RuntimeException("出现了半包问题");
                }



                if (buffer.getBytes().length < messageLength) {
                    System.out.println("半包, length = " + buffer.getBytes().length);
                    return;
                }
                if (buffer.getBytes().length > messageLength) {
                    System.out.println("粘包, length = " + buffer.getBytes().length);
                    return;
                }
                // 解决粘包
                // 每次只读取需要的长度的数据
                String str = new String(buffer.getBytes(0, messageLength));
                System.out.println(str);
                if (testMessage.equals(str)) {
                    System.out.println("good");
                }
            });
        });
        netServer.listen(port, "127.0.0.1", result -> {
                    if (result.succeeded()) {
                        System.out.println("TCP server started on port 8080");
                    } else {
                        System.err.println("Failed to start TCP server: " + result.cause());
                    }
                }
        );

    }

    public static void main(String[] args) {
        VertxTcpServerTest server = new VertxTcpServerTest();
        server.start(8886);
    }
}
