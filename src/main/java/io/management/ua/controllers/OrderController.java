package io.management.ua.controllers;

import io.management.ua.orders.dto.CreateOrderDTO;
import io.management.ua.orders.dto.OrderFilter;
import io.management.ua.orders.service.OrderService;
import io.management.ua.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping
    public Response<?> getOrders(@RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer size,
                                 @RequestParam(required = false) String sortBy,
                                 @RequestParam(required = false) Sort.Direction direction,
                                 @RequestBody OrderFilter orderFilter) {
        return Response.ok(orderService.getOrders(page, size, sortBy, direction, orderFilter));
    }

    @PostMapping
    public Response<?> saveOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        return Response.ok(orderService.saveOrder(createOrderDTO));
    }
}
