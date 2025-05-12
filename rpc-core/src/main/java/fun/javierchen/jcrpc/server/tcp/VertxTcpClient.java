package fun.javierchen.jcrpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/**
 * 用于测试粘包半包
 */
public class VertxTcpClient {
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
           }  else {
               System.err.println("Connect failed: " + result.cause());
           }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        new VertxTcpClient().start();
    }
}
