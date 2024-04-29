package io.management.ua.utility.api.nova.post.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.management.ua.address.attributes.AddressPart;
import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.products.model.ProductDeliveryModel;
import io.management.ua.utility.UtilManager;
import io.management.ua.utility.api.nova.post.models.CargoType;
import io.management.ua.utility.api.nova.post.models.NPDepartmentType;
import io.management.ua.utility.api.nova.post.models.NPRestrictions;
import io.management.ua.utility.api.nova.post.models.http.requests.*;
import io.management.ua.utility.api.nova.post.models.http.response.GeneralResponseModel;
import io.management.ua.utility.api.nova.post.models.http.response.GetCitiesResponse;
import io.management.ua.utility.api.nova.post.models.http.response.GetCityRefResponse;
import io.management.ua.utility.api.nova.post.models.http.response.GetDocumentPriceResponse;
import io.management.ua.utility.api.nova.post.models.http.response.warehouses.WarehouseModel;
import io.management.ua.utility.api.nova.post.utility.Methods;
import io.management.ua.utility.models.NetworkResponse;
import io.management.ua.utility.network.NetworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NovaPostDeliveryService {
    private final NetworkService networkService;

    @Value("${api.delivery.nova.token}")
    private String token;
    @Value("${api.delivery.nova.host}")
    private String host;

    @Cacheable(cacheNames = "novaPostDeliveryCostCache", key = "#productDeliveryModel.width + " +
            "#productDeliveryModel.length + " +
            "#productDeliveryModel.weight + " +
            "#productDeliveryModel.height +" +
            "#deliveryParameters.address.get(T(io.management.ua.address.attributes.AddressPart).CITY_SENDER) +" +
            "#deliveryParameters.address.get(T(io.management.ua.address.attributes.AddressPart).CITY_RECIPIENT)",
    condition = "#result != null && #result.doubleValue() > 0")
    public BigDecimal calculateDeliveryCost(ProductDeliveryModel productDeliveryModel, OrderShipmentAddressDTO deliveryParameters) {
        BigInteger shipmentSeats = calculateShipmentSeats(productDeliveryModel.getWeight(),
                productDeliveryModel.getHeight(),
                productDeliveryModel.getLength(),
                productDeliveryModel.getWidth(),
                NPDepartmentType.byId(UUID.fromString(deliveryParameters.getAddress().get(AddressPart.POST_DEPARTMENT_ID_RECIPIENT))));

        GeneralRequestModel<GetDocumentPriceModel> requestBody =
                getGetDocumentPriceModelGeneralRequestModel(productDeliveryModel, deliveryParameters, shipmentSeats);

        try {
            NetworkResponse networkResponse =
                    networkService.performRequest(HttpMethod.POST, host, new HashMap<>(), requestBody);
            if (networkResponse.getHttpStatus() == HttpStatus.OK) {
                GeneralResponseModel<GetDocumentPriceResponse> response =
                        UtilManager.objectMapper().readValue((String) networkResponse.getBody(),
                                new TypeReference<>(){});

                if (response.isSuccess()) {
                    return BigDecimal.valueOf(response.getData().get(0).getCost());
                } else {
                    throw new RuntimeException("Invalid response from API: " + response.getErrors());
                }
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private GeneralRequestModel<GetDocumentPriceModel> getGetDocumentPriceModelGeneralRequestModel(ProductDeliveryModel productDeliveryModel, OrderShipmentAddressDTO deliveryParameters, BigInteger shipmentSeats) {
        GeneralRequestModel<GetDocumentPriceModel> requestBody = new GeneralRequestModel<>(token);
        requestBody.setCalledMethod(Methods.DELIVERY_COST.getFirst());
        requestBody.setModelName(Methods.DELIVERY_COST.getSecond());
        requestBody.setApiKey(token);

        GetDocumentPriceModel getDocumentPriceModel = new GetDocumentPriceModel();

        if (!deliveryParameters.getAddress().containsKey(AddressPart.CITY_SENDER) || !deliveryParameters.getAddress().containsKey(AddressPart.CITY_RECIPIENT)) {
            throw new RuntimeException("Missing required address information. Sender city or recipient region is not specified");
        }

        String senderCityRef = getCityRef(deliveryParameters.getAddress().get(AddressPart.CITY_SENDER));
        String recipientCityRef = getCityRef(deliveryParameters.getAddress().get(AddressPart.CITY_RECIPIENT));

        getDocumentPriceModel.setCitySender(senderCityRef);
        getDocumentPriceModel.setCityRecipient(recipientCityRef);
        getDocumentPriceModel.setWeight(productDeliveryModel.getWeight());
        getDocumentPriceModel.setServiceType("WarehouseWarehouse");
        getDocumentPriceModel.setCost(productDeliveryModel.getCost());

        if (productDeliveryModel.getWeight().compareTo(BigDecimal.valueOf(30L)) > 0) {
            getDocumentPriceModel.setCargoType(CargoType.CARGO);
        } else {
            getDocumentPriceModel.setCargoType(CargoType.PARCEL);
        }
        getDocumentPriceModel.setSeatsAmount(shipmentSeats.toString());

        requestBody.setMethodProperties(getDocumentPriceModel);

        return requestBody;
    }

    private BigInteger calculateShipmentSeats(BigDecimal shipmentWeight,
                                              BigDecimal shipmentHeight,
                                              BigDecimal shipmentLength,
                                              BigDecimal shipmentWidth,
                                              NPDepartmentType departmentType) {
        switch (departmentType) {
            case DEPARTMENT -> {
                BigInteger seats = NPRestrictions.department.maxWeightPerPlace()
                        .divide(shipmentWeight, RoundingMode.FLOOR).toBigInteger();
                if (seats.compareTo(BigInteger.ONE) < 1) {
                    if (shipmentHeight.compareTo(NPRestrictions.department.maxHeightPerPlace()) > 0
                            || shipmentLength.compareTo(NPRestrictions.department.maxLengthPerPlace()) > 0
                            || shipmentWidth.compareTo(NPRestrictions.department.maxWidthPerPlace()) > 0) {
                        return BigInteger.TWO;
                    } else {
                        return BigInteger.ONE;
                    }
                } else {
                    return BigInteger.ONE;
                }
            }
            case CARGO_DEPARTMENT -> {
                BigInteger seats = NPRestrictions.cargoDepartment.maxWeightPerPlace()
                        .divide(shipmentWeight, RoundingMode.FLOOR).toBigInteger();
                if (seats.compareTo(BigInteger.ONE) < 1) {
                    if (shipmentHeight.compareTo(NPRestrictions.cargoDepartment.maxHeightPerPlace()) > 0
                            || shipmentLength.compareTo(NPRestrictions.cargoDepartment.maxLengthPerPlace()) > 0
                            || shipmentWidth.compareTo(NPRestrictions.cargoDepartment.maxWidthPerPlace()) > 0) {
                        return BigInteger.TWO;
                    } else {
                        return BigInteger.ONE;
                    }
                } else {
                    return BigInteger.ONE;
                }
            }
            case POS_TERMINAL -> {
                BigInteger seats = NPRestrictions.posterminalRestriction.maxWeightPerPlace()
                        .divide(shipmentWeight, RoundingMode.FLOOR).toBigInteger();
                if (seats.compareTo(BigInteger.ONE) < 1) {
                    if (shipmentHeight.compareTo(NPRestrictions.posterminalRestriction.maxHeightPerPlace()) > 0
                            || shipmentLength.compareTo(NPRestrictions.posterminalRestriction.maxLengthPerPlace()) > 0
                            || shipmentWidth.compareTo(NPRestrictions.posterminalRestriction.maxWidthPerPlace()) > 0) {
                        return BigInteger.TWO;
                    } else {
                        return BigInteger.ONE;
                    }
                } else {
                    return BigInteger.ONE;
                }
            }
            case SMALL_DEPARTMENT -> {
                BigInteger seats = NPRestrictions.smallDepartment.maxWeightPerPlace().divide(shipmentWeight, RoundingMode.FLOOR).toBigInteger();
                if (seats.compareTo(BigInteger.ONE) < 1) {
                    if (shipmentHeight.compareTo(NPRestrictions.smallDepartment.maxHeightPerPlace()) > 0
                            || shipmentLength.compareTo(NPRestrictions.smallDepartment.maxLengthPerPlace()) > 0
                            || shipmentWidth.compareTo(NPRestrictions.smallDepartment.maxWidthPerPlace()) > 0) {
                        return BigInteger.TWO;
                    } else {
                        return BigInteger.ONE;
                    }
                } else {
                    return BigInteger.ONE;
                }
            }
        }

        throw new RuntimeException("Unknown department type");
    }

    @Cacheable(cacheNames = "novaPostCityRefCache", key = "#cityName")
    public String getCityRef(String cityName) {
        if (cityName == null
                || cityName.isEmpty()
                || !cityName.matches("^[А-ЩЬЮЯҐЄІЇа-щьюяґєії]+(?:[-\\\\s][А-ЩЬЮЯҐЄІЇа-щьюяґєії]+)*$")) {
            throw new RuntimeException("Invalid city name detected");
        }

        GeneralRequestModel<GetCityRefModel> requestBody = new GeneralRequestModel<>(token);
        requestBody.setCalledMethod(Methods.CITY_ID.getFirst());
        requestBody.setModelName(Methods.CITY_ID.getSecond());
        requestBody.setApiKey(token);

        GetCityRefModel getCitiesModel = new GetCityRefModel();
        getCitiesModel.setPage("1");
        getCitiesModel.setCity(cityName);
        getCitiesModel.setLimit("1");

        requestBody.setMethodProperties(getCitiesModel);

        try {
            NetworkResponse networkResponse =
                    networkService.performRequest(HttpMethod.POST, host, new HashMap<>(), requestBody);
            if (networkResponse.getHttpStatus() == HttpStatus.OK) {
                GeneralResponseModel<GetCityRefResponse> response = UtilManager.objectMapper().readValue((String) networkResponse.getBody(),
                        new TypeReference<>() {
                        });

                if (response.isSuccess()) {
                    return response.getData().get(0).getRef();
                } else {
                    throw new RuntimeException("Invalid response from API: " + response.getErrors());
                }
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<GetCitiesResponse> getCities(String findByString,
                                             @DefaultNumberValue Integer page,
                                             @DefaultNumberValue(number = 100) Integer limit) {
        GeneralRequestModel<GetCitiesModel> requestBody = new GeneralRequestModel<>(token);
        requestBody.setCalledMethod(Methods.CITIES.getFirst());
        requestBody.setModelName(Methods.CITIES.getSecond());
        requestBody.setApiKey(token);

        GetCitiesModel getCitiesModel = new GetCitiesModel();
        getCitiesModel.setFindByString(findByString);
        getCitiesModel.setPage(String.valueOf(page));
        getCitiesModel.setWarehouse("1");
        getCitiesModel.setLimit(String.valueOf(limit));

        requestBody.setMethodProperties(getCitiesModel);

        try {
            NetworkResponse networkResponse =
                    networkService.performRequest(HttpMethod.POST, host, new HashMap<>(), requestBody);
            if (networkResponse.getHttpStatus() == HttpStatus.OK) {
                GeneralResponseModel<GetCitiesResponse> response = UtilManager.objectMapper().readValue((String) networkResponse.getBody(),
                        new TypeReference<>() {
                        });

                if (response.isSuccess()) {
                    return response.getData();
                } else {
                    throw new RuntimeException("Invalid response from API: " + response.getErrors());
                }
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<WarehouseModel> getWarehouses(String cityName,
                                              String findByString,
                                              @DefaultNumberValue Integer page,
                                              @DefaultNumberValue(number = 100) Integer limit) {
        GeneralRequestModel<GetWarehousesModel> requestModel = new GeneralRequestModel<>();
        requestModel.setApiKey(token);
        requestModel.setCalledMethod(Methods.WAREHOUSES.getFirst());
        requestModel.setModelName(Methods.WAREHOUSES.getSecond());

        GetWarehousesModel getWarehousesModel = new GetWarehousesModel();
        getWarehousesModel.setPage(String.valueOf(page));
        getWarehousesModel.setLimit(String.valueOf(limit));
        getWarehousesModel.setCityName(cityName);
        getWarehousesModel.setFindByString(findByString);
        requestModel.setMethodProperties(getWarehousesModel);

        try {
            NetworkResponse networkResponse =
                    networkService.performRequest(HttpMethod.POST, host, new HashMap<>(), requestModel);
            if (networkResponse.getHttpStatus() == HttpStatus.OK) {
                GeneralResponseModel<WarehouseModel> response = UtilManager.objectMapper().readValue((String) networkResponse.getBody(),
                        new TypeReference<>() {
                        });

                if (response.isSuccess()) {
                    return response.getData();
                } else {
                    throw new RuntimeException("Invalid response from API: " + response.getErrors());
                }
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return List.of();
    }
}
