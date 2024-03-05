package io.management.ua.utility.api.nova.post.models.http.response.warehouses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WarehouseModel {
    @JsonProperty("SiteKey")
    private String siteKey;
    
    @JsonProperty("Description")
    private String description;
    
    @JsonProperty("DescriptionRu")
    private String descriptionRu;
    
    @JsonProperty("ShortAddress")
    private String shortAddress;
    
    @JsonProperty("ShortAddressRu")
    private String shortAddressRu;
    
    @JsonProperty("Phone")
    private String phone;
    
    @JsonProperty("TypeOfWarehouse")
    private String typeOfWarehouse;
    
    @JsonProperty("Ref")
    private String ref;
    
    @JsonProperty("Number")
    private String number;
    
    @JsonProperty("CityRef")
    private String cityRef;
    
    @JsonProperty("CityDescription")
    private String cityDescription;
    
    @JsonProperty("CityDescriptionRu")
    private String cityDescriptionRu;
    
    @JsonProperty("SettlementRef")
    private String settlementRef;
    
    @JsonProperty("SettlementDescription")
    private String settlementDescription;
    
    @JsonProperty("SettlementAreaDescription")
    private String settlementAreaDescription;
    
    @JsonProperty("SettlementRegionsDescription")
    private String settlementRegionsDescription;
    
    @JsonProperty("SettlementTypeDescription")
    private String settlementTypeDescription;
    
    @JsonProperty("SettlementTypeDescriptionRu")
    private String settlementTypeDescriptionRu;
    
    @JsonProperty("Longitude")
    private String longitude;
    
    @JsonProperty("Latitude")
    private String latitude;
    
    @JsonProperty("PostFinance")
    private String postFinance;
    
    @JsonProperty("BicycleParking")
    private String bicycleParking;
    
    @JsonProperty("PaymentAccess")
    private String paymentAccess;
    
    @JsonProperty("POSTerminal")
    private String postTerminal;
    
    @JsonProperty("InternationalShipping")
    private String internationalShipping;
    
    @JsonProperty("SelfServiceWorkplacesCount")
    private String selfServiceWorkplacesCount;
    
    @JsonProperty("TotalMaxWeightAllowed")
    private String totalMaxWeightAllowed;
    
    @JsonProperty("PlaceMaxWeightAllowed")
    private String placeMaxWeightAllowed;
    
    @JsonProperty("SendingLimitationsOnDimensions")
    private Dimensions sendingLimitationsOnDimensions;
    
    @JsonProperty("ReceivingLimitationsOnDimensions")
    private Dimensions receivingLimitationsOnDimensions;
    
    @JsonProperty("Reception")
    private Schedule reception;
    
    @JsonProperty("Delivery")
    private Schedule delivery;
    
    @JsonProperty("Schedule")
    private Schedule schedule;
    
    @JsonProperty("DistrictCode")
    private String districtCode;
    
    @JsonProperty("WarehouseStatus")
    private String warehouseStatus;
    
    @JsonProperty("WarehouseStatusDate")
    private String warehouseStatusDate;
    
    @JsonProperty("WarehouseIllusha")
    private String warehouseIllusha;
    
    @JsonProperty("CategoryOfWarehouse")
    private String categoryOfWarehouse;
    
    @JsonProperty("Direct")
    private String direct;
    
    @JsonProperty("RegionCity")
    private String regionCity;
    
    @JsonProperty("WarehouseForAgent")
    private String warehouseForAgent;
    
    @JsonProperty("GeneratorEnabled")
    private String generatorEnabled;
    
    @JsonProperty("MaxDeclaredCost")
    private String maxDeclaredCost;
    
    @JsonProperty("WorkInMobileAwis")
    private String workInMobileAwis;
    
    @JsonProperty("DenyToSelect")
    private String denyToSelect;
    
    @JsonProperty("CanGetMoneyTransfer")
    private String canGetMoneyTransfer;
    
    @JsonProperty("HasMirror")
    private String hasMirror;
    
    @JsonProperty("HasFittingRoom")
    private String hasFittingRoom;
    
    @JsonProperty("OnlyReceivingParcel")
    private String onlyReceivingParcel;
    
    @JsonProperty("PostMachineType")
    private String postMachineType;
    
    @JsonProperty("PostalCodeUA")
    private String postalCodeUA;
    
    @JsonProperty("WarehouseIndex")
    private String warehouseIndex;
    
    @JsonProperty("BeaconCode")
    private String beaconCode;
    
    @JsonProperty("PostomatFor")
    private String postomatFor;
}

@Data
class Dimensions {
    @JsonProperty("Width")
    private int width;
    
    @JsonProperty("Height")
    private int height;
    
    @JsonProperty("Length")
    private int length;
}

@Data
class Schedule {
    @JsonProperty("Monday")
    private String monday;
    
    @JsonProperty("Tuesday")
    private String tuesday;
    
    @JsonProperty("Wednesday")
    private String wednesday;
    
    @JsonProperty("Thursday")
    private String thursday;
    
    @JsonProperty("Friday")
    private String friday;
    
    @JsonProperty("Saturday")
    private String saturday;
    
    @JsonProperty("Sunday")
    private String sunday;
}
