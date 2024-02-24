package io.management.ua.utility.api.service;

import io.management.ua.utility.api.np.service.NovaPostDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final NovaPostDeliveryService novaPostDeliveryService;

    private void validateCalledMethodName(String methodName) {
        Class<RouteService> routeServiceClass = RouteService.class;

        Method[] routeServiceClassMethods = routeServiceClass.getDeclaredMethods();

        if (!Arrays.stream(routeServiceClassMethods).map(Method::getName).toList().contains(methodName)) {
            throw new RuntimeException(String.format("Invalid called method detected: %s", methodName));
        }
    }
}
