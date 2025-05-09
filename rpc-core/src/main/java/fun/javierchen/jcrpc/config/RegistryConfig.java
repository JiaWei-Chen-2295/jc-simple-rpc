package fun.javierchen.jcrpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 框架注册中心配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryConfig {

    private String registry = "etcd";

    private String address = "http://localhost:2380";

    private String username;

    private String password;

    private Long timeout = 1000L;

}
