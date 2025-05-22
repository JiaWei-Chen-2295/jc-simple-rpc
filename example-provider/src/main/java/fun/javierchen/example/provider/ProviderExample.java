package fun.javierchen.example.provider;

import fun.javierchen.example.common.service.OrderService;
import fun.javierchen.jcrpc.bootstrap.ProviderBootstrap;
import fun.javierchen.jcrpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 更简单的提供者
 */
public class ProviderExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(
                OrderService.class.getName(),
                OrderServiceImpl.class
        );
        serviceRegisterInfoList.add(serviceRegisterInfo);
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
