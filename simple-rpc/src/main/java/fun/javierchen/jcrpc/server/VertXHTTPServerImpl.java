package fun.javierchen.jcrpc.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class VertXHTTPServerImpl implements HTTPServer {
    @Override
    public void start(int serverPort) {
        Vertx vertx = Vertx.vertx();

        HttpServer httpServer = vertx.createHttpServer();

        // 监听端口并使用我们自定义的类来处理请求
        httpServer.requestHandler(new HTTPServerHandler());

        // 服务器真正启动
        httpServer.listen(serverPort, result -> {
            if (result.succeeded()) {
                System.out.println("Vertx listening port: " + serverPort + "Success !!");
            }
            if (result.failed()) {
                System.out.println("Vertx listening port: " + serverPort + "Fail !!!!!");
            }
         });

    }
}
