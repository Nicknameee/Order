package io.management.ua.controllers;

import io.management.ua.orders.dto.CreateOrderDTO;
import io.management.ua.orders.dto.OrderFilter;
import io.management.ua.orders.dto.UpdateOrderDTO;
import io.management.ua.orders.service.OrderService;
import io.management.ua.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping
    public Response<?> getOrders(@RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer size,
                                 @RequestParam(required = false) String sortBy,
                                 @RequestParam(required = false) String direction,
                                 @RequestBody(required = false) OrderFilter orderFilter) {
        return Response.ok(orderService.getOrders(orderFilter, page, size, sortBy, direction));
    }

    @PostMapping("/save")
    public Response<?> saveOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        return Response.ok(orderService.saveOrder(createOrderDTO));
    }

    @PutMapping("/update")
    public Response<?> updateOrder(@RequestBody UpdateOrderDTO updateOrderDTO) {
        return Response.ok(orderService.updateOrder(updateOrderDTO));
    }

    @GetMapping("/history")
    public Response<?> getOrderHistory(@RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size,
                                       @RequestParam(required = false) String sortBy,
                                       @RequestParam(required = false) String direction,
                                       @RequestBody(required = false) OrderFilter orderFilter) {
        return Response.ok(orderService.getOrderHistory(page, size, sortBy, direction, orderFilter));
    }

    @GetMapping("/export")
    public void exportOrderData(@RequestBody(required = false) OrderFilter orderFilter,
                                @RequestParam(required = false) String filename,
                                HttpServletResponse httpServletResponse) {
        orderService.exportOrders(orderFilter, filename, httpServletResponse);
    }
}
