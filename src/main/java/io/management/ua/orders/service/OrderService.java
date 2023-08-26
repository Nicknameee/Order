package io.management.ua.orders.service;

import io.management.ua.orders.entity.OrderModel;
import io.management.ua.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderModel> getOrders(int page, int size, String sortBy, Sort.Direction direction) {
        return orderRepository.getOrderModelsByIdIsNotNull(PageRequest.of(size, page, Sort.by(direction, sortBy)))
                .toList();
    }

    public List<OrderModel> getCustomerOrders(Long customerId, int page, int size, String sortBy, Sort.Direction direction) {
        return orderRepository.getOrderModelsByCustomerId(customerId,
                        PageRequest.of(size, page, Sort.by(direction, sortBy)))
                .toList();
    }
}
