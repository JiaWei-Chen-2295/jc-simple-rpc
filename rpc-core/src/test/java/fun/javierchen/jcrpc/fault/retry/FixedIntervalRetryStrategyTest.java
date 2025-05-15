package fun.javierchen.jcrpc.fault.retry;

import fun.javierchen.jcrpc.model.RpcResponse;
import junit.framework.TestCase;
import org.junit.Test;

public class FixedIntervalRetryStrategyTest extends TestCase {
    @Test
    public void testDoRetry() throws Exception {
        RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RetryStrategyKeys.FIXED_INTERVAL);
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}

