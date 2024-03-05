package io.management.ua.orders.service;

import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.products.entity.Product;
import io.management.ua.products.model.ProductDeliveryModel;
import io.management.ua.products.model.ProductParameter;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import io.management.ua.utility.api.nova.post.service.NovaPostDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {
    private final NovaPostDeliveryService novaPostDeliveryService;

    public BigDecimal getDeliveryCost(Pair<Product, Integer> orderedProducts, DeliveryServiceType deliveryServiceType, OrderShipmentAddressDTO deliveryParameters) {
        ProductDeliveryModel productDeliveryModel = processOrderedProductsForDelivery(orderedProducts);

        switch (deliveryServiceType) {
            case NOVA_POST -> {
                return novaPostDeliveryService.calculateDeliveryCost(productDeliveryModel, deliveryParameters);
            }
            default -> {
                return null;
            }
        }

    }

    private ProductDeliveryModel processOrderedProductsForDelivery(Pair<Product, Integer> orderedProduct) {
        Product product = orderedProduct.getFirst();
        ProductDeliveryModel productDeliveryModel = new ProductDeliveryModel();

        BigDecimal weight = new BigDecimal(product.getParameter(ProductParameter.WEIGHT));
        BigDecimal width = new BigDecimal(product.getParameter(ProductParameter.WIDTH));
        BigDecimal length = new BigDecimal(product.getParameter(ProductParameter.LENGTH));
        BigDecimal height = new BigDecimal(product.getParameter(ProductParameter.HEIGHT));

        productDeliveryModel.setWeight(weight);
        productDeliveryModel.setWidth(width);
        productDeliveryModel.setLength(length);
        productDeliveryModel.setHeight(height);
        productDeliveryModel.setCost(product.getCost().multiply(BigDecimal.valueOf(orderedProduct.getSecond())));

        return productDeliveryModel;
    }
}
