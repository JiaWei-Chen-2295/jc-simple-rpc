package fun.javierchen.jcrpc.server;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

import java.nio.charset.StandardCharsets;

public class VertxTcpServerTest {

    public void start(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(handler -> {
            handler.handler(buffer -> {
                String testMessage = "Hello, server!Hello, server!Hello, server!";
                int messageLength = testMessage.getBytes().length;

                // 使用 RecordParser 解决半包和粘包
                RecordParser parser = RecordParser.newFixed(messageLength);
                parser.setOutput(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        String str = new String(buffer.getBytes());
                        System.out.println(str);
                        if (testMessage.equals(str)) {
                            System.out.println("good");
                        }
                    }
                });
                handler.handler(parser);
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
