package io.management.ua.orders.attributes;

import io.management.ua.utility.enums.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    INITIATED(Map.of(Locale.EN, "Order created and taken into processing")),
    ASSIGNED_TO_OPERATOR(Map.of(Locale.EN, "Order assigned to operator")),
    WAITING_FOR_PAYMENT(Map.of(Locale.EN, "Waiting for payment")),
    PAID(Map.of(Locale.EN, "Order is paid")),
    SHIPPED(Map.of(Locale.EN, "Order passed to delivery service")),
    IN_DELIVERY_PROCESS(Map.of(Locale.EN, "Order is on the road")),
    DELIVERED(Map.of(Locale.EN, "Order delivered to the destination")),
    RETURNED(Map.of(Locale.EN , "Order has been returned by delivery service")),
    RECEIVED(Map.of(Locale.EN, "Order is received")),
    DECLINED(Map.of(Locale.EN, "Order is declined"));

    private final Map<Locale, String> statusDescriptions;

    public static final Set<OrderStatus> prepaymentTransitionChain = Set.of(INITIATED, WAITING_FOR_PAYMENT, PAID, ASSIGNED_TO_OPERATOR, SHIPPED, IN_DELIVERY_PROCESS, DELIVERED, RECEIVED);
    public static final Set<OrderStatus> CODTransitionChain = Set.of(INITIATED, ASSIGNED_TO_OPERATOR, SHIPPED, IN_DELIVERY_PROCESS, DELIVERED, RECEIVED);

    public static final Set<OrderStatus> completedStatus = Set.of(DECLINED, RECEIVED, RETURNED);

}
