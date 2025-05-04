package fun.javierchen.jcrpc.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String name = "jc-rpc";

    private String version = "1.0";

    private String serverHost = "localhost";

    private int serverPort = 8080;

    private boolean useMock = false;

    private String serializerType = "jdk";

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

}
