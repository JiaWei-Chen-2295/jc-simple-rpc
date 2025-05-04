package fun.javierchen.jcrpc.model;

import fun.javierchen.jcrpc.constant.RpcConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务注册的名字
     */
    private String serviceName;
    /**
     * 需要调用的方法名
     */
    private String methodName;

    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypeList;
    /**
     * 参数列表
     */
    private Object[] args;
}
