package io.management.ua.orders.service;

import io.management.ua.annotations.DefaultValue;
import io.management.ua.exceptions.ActionRestrictedException;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.dto.OrderDTO;
import io.management.ua.orders.dto.OrderFilter;
import io.management.ua.orders.entity.OrderModel;
import io.management.ua.orders.mapper.OrderMapper;
import io.management.ua.orders.repository.OrderRepository;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final EntityManager entityManager;

    public List<OrderModel> getOrders(@DefaultValue(value = "1") Integer page,
                                      @DefaultValue("1") Integer size,
                                      @DefaultValue("orderDate") String sortBy,
                                      @DefaultValue("\"ASC\"") Sort.Direction direction,
                                      OrderFilter orderFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderModel> query = criteriaBuilder.createQuery(OrderModel.class);
        Root<OrderModel> root = query.from(OrderModel.class);

        List<Predicate> predicates = new ArrayList<>();

        if (orderFilter.getOrderId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.id), orderFilter.getOrderId()));
        }
        if (orderFilter.getOrderNumber() != null) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.orderNumber), orderFilter.getOrderNumber()));
        }
        if (orderFilter.getTotalProductCostFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderModel.Fields.totalProductCost), orderFilter.getTotalProductCostFrom()));
        }
        if (orderFilter.getTotalProductCostTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OrderModel.Fields.totalProductCost), orderFilter.getTotalProductCostTo()));
        }
        if (orderFilter.getTotalDeliveryCostFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderModel.Fields.deliveryCost), orderFilter.getTotalDeliveryCostFrom()));
        }
        if (orderFilter.getTotalDeliveryCostTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OrderModel.Fields.deliveryCost), orderFilter.getTotalDeliveryCostTo()));
        }
        if (orderFilter.getOrderDateFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderModel.Fields.orderDate), orderFilter.getOrderDateFrom()));
        }
        if (orderFilter.getOrderDateTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OrderModel.Fields.orderDate), orderFilter.getOrderDateTo()));
        }
        if (orderFilter.getOrderStatuses() != null && !orderFilter.getOrderStatuses().isEmpty()) {
            predicates.add(root.get(OrderModel.Fields.orderStatus).in(orderFilter.getOrderStatuses()));
        }
        if (orderFilter.getPaymentType() != null) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.paymentType), orderFilter.getPaymentType()));
        }
        if (orderFilter.getCustomerId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.customerId), orderFilter.getCustomerId()));
        }
        if (orderFilter.getProcessingOperatorId() != null) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.processingOperatorId), orderFilter.getProcessingOperatorId()));
        }
        if (orderFilter.getPaid() != null) {
            if (orderFilter.getPaid()) {
                predicates.add(criteriaBuilder.isTrue(root.get(OrderModel.Fields.paid)));
            } else {
                predicates.add(criteriaBuilder.isFalse(root.get(OrderModel.Fields.paid)));
            }
        }
        if (orderFilter.getOrderedProducts() != null && !orderFilter.getOrderedProducts().isEmpty()) {
            Join<OrderModel, List<ProductModel>> joinProducts = root.join(OrderModel.Fields.orderedProducts);
            predicates.add(joinProducts.get(ProductModel.Fields.id).in(orderFilter.getOrderedProducts()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        switch (direction) {
            case DESC -> query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
            case ASC -> query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
        }

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    public OrderModel getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("Order with ID: %s was not found", orderId)));
    }

    @Transactional
    public OrderModel saveOrder(OrderDTO orderDTO) {
        OrderModel orderModel = orderMapper.dtoToEntity(orderDTO);

        BigDecimal productCost = productService.calculateProductsTotalCost(orderDTO.getOrderedProducts());

        orderModel.setTotalProductCost(productCost);
        orderModel = orderRepository.save(orderModel);

        productService.clearOrderedProducts(orderModel.getId());
        productService.orderProducts(orderDTO.getOrderedProducts(), orderModel.getId());

        return orderModel;
    }

    public void updateOrderStatus(Long orderId, OrderStatus nextStatus) {
        OrderModel orderModel = getOrderById(orderId);

        if (OrderStatus.checkTransitionRule(orderModel.getOrderStatus(), nextStatus, orderModel.getPaid())) {
            orderModel.setOrderStatus(nextStatus);
            orderRepository.save(orderModel);
        } else {
            throw new ActionRestrictedException(String.format("Order can not be transferred to the status named: %s", nextStatus));
        }
    }
}
