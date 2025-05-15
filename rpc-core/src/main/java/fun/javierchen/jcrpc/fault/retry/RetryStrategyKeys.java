package fun.javierchen.jcrpc.fault.retry;

public interface RetryStrategyKeys {
    /**
     * 不重试策略
     */
    String NO_RETRY = "no_retry";
    /**
     * 固定间隔重试策略
     */
    String FIXED_INTERVAL = "fixed_interval";
}
