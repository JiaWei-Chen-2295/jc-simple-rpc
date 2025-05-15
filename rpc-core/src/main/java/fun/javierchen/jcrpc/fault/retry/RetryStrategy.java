package fun.javierchen.jcrpc.fault.retry;

import fun.javierchen.jcrpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

    /**
     * 进行重试
     * @param callable
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;

}
