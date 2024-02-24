package io.management.ua.orders.service;

import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.address.service.OrderShipmentAddressService;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.annotations.DefaultStringValue;
import io.management.ua.exceptions.ActionRestrictedException;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.dto.CreateOrderDTO;
import io.management.ua.orders.dto.OrderFilter;
import io.management.ua.orders.entity.OrderModel;
import io.management.ua.orders.mapper.OrderMapper;
import io.management.ua.orders.repository.OrderRepository;
import io.management.ua.products.dto.OrderedProductDTO;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.products.service.ProductService;
import io.management.ua.utility.TimeUtil;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
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
    private final OrderShipmentAddressService orderShipmentAddressService;
    private final DeliveryService deliveryService;
    private final EntityManager entityManager;

    public List<OrderModel> getOrders(@DefaultNumberValue Integer page,
                                      @DefaultNumberValue Integer size,
                                      @DefaultStringValue(string = "orderDate") String sortBy,
                                      @DefaultStringValue(string = "\"ASC\"") Sort.Direction direction,
                                      OrderFilter orderFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderModel> query = criteriaBuilder.createQuery(OrderModel.class);
        Root<OrderModel> root = query.from(OrderModel.class);

        List<Predicate> predicates = new ArrayList<>();

        if (orderFilter.getId() != null && orderFilter.getId() > 0) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.id), orderFilter.getId()));
        }

        if (orderFilter.getCustomerId() != null && orderFilter.getCustomerId() > 0) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.customerId), orderFilter.getCustomerId()));
        }

        if (orderFilter.getTotalDeliveryCostFrom() != null && orderFilter.getTotalDeliveryCostFrom().compareTo(BigDecimal.ZERO) > 0) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderModel.Fields.deliveryCost), orderFilter.getTotalDeliveryCostFrom()));
        }

        if (orderFilter.getTotalDeliveryCostTo() != null && orderFilter.getTotalDeliveryCostTo().compareTo(BigDecimal.ZERO) > 0) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OrderModel.Fields.deliveryCost), orderFilter.getTotalDeliveryCostTo()));
        }

        if (orderFilter.getOrderNumber() != null) {
            predicates.add(criteriaBuilder.equal(root.get(OrderModel.Fields.number), orderFilter.getOrderNumber()));
        }

        if (orderFilter.getOrderedProductsCostFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderModel.Fields.orderedProductCost), orderFilter.getOrderedProductsCostFrom()));
        }

        if (orderFilter.getOrderedProductsCostTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OrderModel.Fields.orderedProductCost), orderFilter.getOrderedProductsCostTo()));
        }

        if (orderFilter.getOrderDateFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(OrderModel.Fields.creationDate), orderFilter.getOrderDateFrom()));
        }

        if (orderFilter.getOrderDateTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(OrderModel.Fields.creationDate), orderFilter.getOrderDateTo()));
        }

        if (orderFilter.getOrderStatuses() != null && !orderFilter.getOrderStatuses().isEmpty()) {
            predicates.add(root.get(OrderModel.Fields.status).in(orderFilter.getOrderStatuses()));
        }

        if (orderFilter.getPaymentTypes() != null && !orderFilter.getPaymentTypes().isEmpty()) {
            predicates.add(root.get(OrderModel.Fields.paymentType).in(orderFilter.getPaymentTypes()));
        }

        if (orderFilter.getProcessingOperatorIds() != null && !orderFilter.getProcessingOperatorIds().isEmpty()) {
            predicates.add(root.get(OrderModel.Fields.processingOperatorId).in(orderFilter.getProcessingOperatorIds()));
        }

        if (orderFilter.getPaid() != null) {
            if (orderFilter.getPaid()) {
                predicates.add(criteriaBuilder.isTrue(root.get(OrderModel.Fields.paid)));
            } else {
                predicates.add(criteriaBuilder.isFalse(root.get(OrderModel.Fields.paid)));
            }
        }

        if (orderFilter.getOrderedProductIds() != null && !orderFilter.getOrderedProductIds().isEmpty()) {
            Join<OrderModel, List<ProductModel>> joinProducts = root.join(OrderModel.Fields.orderedProducts);
            predicates.add(joinProducts.get(ProductModel.Fields.id).in(orderFilter.getOrderedProductIds()));
        }

        if (orderFilter.getProductNames() != null && !orderFilter.getProductNames().isEmpty()) {
            Join<OrderModel, List<ProductModel>> joinProducts = root.join(OrderModel.Fields.orderedProducts);
            predicates.add(joinProducts.get(ProductModel.Fields.name).in(orderFilter.getProductNames()));
        }

        if (orderFilter.getVendorIds() != null && !orderFilter.getVendorIds().isEmpty()) {
            Join<OrderModel, List<ProductModel>> joinProducts = root.join(OrderModel.Fields.orderedProducts);
            predicates.add(joinProducts.get(ProductModel.Fields.name).in(orderFilter.getVendorIds()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        switch (direction) {
            case DESC -> query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
            case ASC -> query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
        }

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    public OrderModel getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order with ID: %s was not found", id)));
    }

    /**
     * Order Processing Logic
     * For each unique product item new order will be created
     * Reasons: customer can select different products from different vendors, with different delivery settings, for correct processing they are going to be split
     */
    @Transactional
    public List<OrderModel> saveOrder(CreateOrderDTO createOrderDTO) {
        if (createOrderDTO.getOrderedProducts() == null || createOrderDTO.getOrderedProducts().isEmpty()) {
            throw new RuntimeException("Invalid order model, no ordered products");
        }
        if (createOrderDTO.getDeliveryServiceType() == null) {
            throw new RuntimeException("Invalid order delivery model, no information about delivery process");
        }

        List<OrderModel> orders = new ArrayList<>();

        for (OrderedProductDTO orderedProductDTO : createOrderDTO.getOrderedProducts()) {
            OrderModel orderModel = orderMapper.dtoToEntity(createOrderDTO);

            Pair<ProductModel, Integer> orderedProduct = productService.orderProduct(orderedProductDTO, orderModel.getId());

            BigDecimal productCost = orderedProduct.getFirst().getCost()
                    .multiply(BigDecimal.valueOf(orderedProduct.getSecond()));

            if (createOrderDTO.getDeliveryServiceType() != DeliveryServiceType.NONE) {
                BigDecimal deliveryCost =
                        deliveryService.getDeliveryCost(orderedProduct, createOrderDTO.getDeliveryServiceType(), createOrderDTO.getOrderShipmentAddress());
                orderModel.setDeliveryCost(deliveryCost);
            }

            orderModel.setOrderedProductCost(productCost);
            orderModel = save(orderModel);

            if (createOrderDTO.getDeliveryServiceType() != DeliveryServiceType.NONE) {
                OrderShipmentAddressDTO orderShipmentAddressDTO = createOrderDTO.getOrderShipmentAddress();
                orderShipmentAddressDTO.setOrderId(orderModel.getId());

                orderShipmentAddressService.saveOrderShipmentAddress(orderShipmentAddressDTO);
            }
        }

        return orders;
    }

    public void updateOrderStatus(Long orderId, OrderStatus nextStatus) {
        OrderModel orderModel = getOrderById(orderId);

        if (OrderStatus.checkTransitionRule(orderModel.getStatus(), nextStatus, orderModel.getPaymentType())) {
            orderModel.setStatus(nextStatus);
            save(orderModel);
        } else {
            throw new ActionRestrictedException(String.format("Order can not be transferred to the status named: %s", nextStatus));
        }
    }

    private OrderModel save(OrderModel orderModel) {
        orderModel.setLastUpdateDate(TimeUtil.getCurrentDateTime());

        return orderRepository.save(orderModel);
    }
}
