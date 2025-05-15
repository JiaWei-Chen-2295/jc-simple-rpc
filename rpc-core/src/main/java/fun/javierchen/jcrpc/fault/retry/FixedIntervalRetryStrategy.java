package fun.javierchen.jcrpc.fault.retry;

import com.github.rholder.retry.*;
import fun.javierchen.jcrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 使用 Guava retry 实现固定间隔重试
 * 策略如下：
 *  1. 默认重试3次
 *  2. 默认重试间隔3秒
 *  3. 默认重试策略为发生异常重试
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .retryIfExceptionOfType(RuntimeException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试{}次", attempt.getAttemptNumber());
                    }
                }).build();


        return retryer.call(callable);
    }
}
