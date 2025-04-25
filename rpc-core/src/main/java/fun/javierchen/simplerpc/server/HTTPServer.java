package fun.javierchen.simplerpc.server;

public interface HTTPServer {
    /**
     * 启动 RPC 服务
     * @param serverPort
     */
    void start(int serverPort);
}
