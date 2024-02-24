package io.management.ua.orders.service;

import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.products.model.ProductDeliveryModel;
import io.management.ua.products.model.ProductParameter;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import io.management.ua.utility.api.np.service.NovaPostDeliveryService;
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

    public BigDecimal getDeliveryCost(Pair<ProductModel, Integer> orderedProducts, DeliveryServiceType deliveryServiceType, OrderShipmentAddressDTO deliveryParameters) {
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

    private ProductDeliveryModel processOrderedProductsForDelivery(Pair<ProductModel, Integer> orderedProduct) {
        ProductModel productModel = orderedProduct.getFirst();
        ProductDeliveryModel productDeliveryModel = new ProductDeliveryModel();

        BigDecimal weight = (BigDecimal) productModel.getParameter(ProductParameter.WEIGHT);
        BigDecimal width = (BigDecimal) productModel.getParameter(ProductParameter.WIDTH);
        BigDecimal length = (BigDecimal) productModel.getParameter(ProductParameter.LENGTH);
        BigDecimal height = (BigDecimal) productModel.getParameter(ProductParameter.HEIGHT);

        productDeliveryModel.setWeight(weight);
        productDeliveryModel.setWidth(width);
        productDeliveryModel.setLength(length);
        productDeliveryModel.setHeight(height);
        productDeliveryModel.setCost(productModel.getCost());

        return productDeliveryModel;
    }
}
