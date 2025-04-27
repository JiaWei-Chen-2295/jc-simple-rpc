package fun.javierchen.example.common.service;

import fun.javierchen.example.common.model.Order;

public interface OrderService {
    /**
     * 获取订单信息
     * @return
     */
    Order getOrder(long orderId);

    /**
     * 用于测试 Mock 接口的返回值
     * 如果需要观察记得打开mock配置
     * @return
     */
    default short getNum() {
        return 1;
    }

    /**
     * 用于测试返回值是对象的mock结果
     * @return
     */
    default Object getObj() {
        return new Object();
    }

}
