package io.management.ua.orders.attributes;

import io.management.ua.utility.enums.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
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

    public static final List<OrderStatus> prepaymentTransitionChain = List.of(INITIATED, WAITING_FOR_PAYMENT, PAID, ASSIGNED_TO_OPERATOR, SHIPPED, IN_DELIVERY_PROCESS, DELIVERED, RECEIVED);
    public static final List<OrderStatus> CODTransitionChain = List.of(INITIATED, ASSIGNED_TO_OPERATOR, SHIPPED, IN_DELIVERY_PROCESS, DELIVERED, RECEIVED);

    public static final Set<OrderStatus> completedStatus = Set.of(DECLINED, RECEIVED, RETURNED);

    public static boolean checkTransitionRule(OrderStatus previousStatus, OrderStatus status, PaymentType paymentType) {
        switch (paymentType) {
            case PREPAYMENT -> {
                Iterator<OrderStatus> prepaidIterator = prepaymentTransitionChain.iterator();

                while (prepaidIterator.hasNext()) {
                    if (prepaidIterator.next() == previousStatus) {
                        if (prepaidIterator.hasNext()) {
                            return (prepaidIterator.next() == status);
                        }
                    }
                }

                return false;
            }
            case COD -> {
                Iterator<OrderStatus> codIterator = CODTransitionChain.iterator();

                while (codIterator.hasNext()) {
                    if (codIterator.next() == previousStatus) {
                        if (codIterator.hasNext()) {
                            return (codIterator.next() == status);
                        }
                    }
                }
            }
        }

        return false;
    }
}
