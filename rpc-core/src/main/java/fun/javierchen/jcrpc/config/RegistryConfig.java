package fun.javierchen.jcrpc.config;

import lombok.Data;

/**
 * 框架注册中心配置
 */
@Data
public class RegistryConfig {

    private String registry = "etcd";

    private String address = "http://localhost:2380";

    private String username;

    private String password;

    private Long timeout = 1000L;


}
