package io.management.ua.utility.api.np.models.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetCitiesResponse {
    /**
     * Ref
     */
    @JsonProperty("Ref")
    private String ref;

    /**
     * SettlementType
     */
    @JsonProperty("SettlementType")
    private String settlementType;

    /**
     * Latitude
     */
    @JsonProperty("Latitude")
    private String latitude;

    /**
     * Longitude
     */
    @JsonProperty("Longitude")
    private String longitude;

    /**
     * Description
     */
    @JsonProperty("Description")
    private String description;

    /**
     * DescriptionTranslit
     */
    @JsonProperty("DescriptionTranslit")
    private String descriptionTranslit;

    /**
     * SettlementTypeDescription
     */
    @JsonProperty("SettlementTypeDescription")
    private String settlementTypeDescription;

    /**
     * SettlementTypeDescriptionTranslit
     */
    @JsonProperty("SettlementTypeDescriptionTranslit")
    private String settlementTypeDescriptionTranslit;

    /**
     * Region
     */
    @JsonProperty("Region")
    private String region;

    /**
     * RegionsDescription
     */
    @JsonProperty("RegionsDescription")
    private String regionsDescription;

    /**
     * RegionsDescriptionTranslit
     */
    @JsonProperty("RegionsDescriptionTranslit")
    private String regionsDescriptionTranslit;

    /**
     * Area
     */
    @JsonProperty("Area")
    private String area;

    /**
     * AreaDescription
     */
    @JsonProperty("AreaDescription")
    private String areaDescription;

    /**
     * AreaDescriptionTranslit
     */
    @JsonProperty("AreaDescriptionTranslit")
    private String areaDescriptionTranslit;

    /**
     * Index1
     */
    @JsonProperty("Index1")
    private String index1;

    /**
     * Index2
     */
    @JsonProperty("Index2")
    private String index2;

    /**
     * IndexCOATSU1
     */
    @JsonProperty("IndexCOATSU1")
    private String indexCOATSU1;

    /**
     * Delivery1
     */
    @JsonProperty("Delivery1")
    private String delivery1;

    /**
     * Delivery2
     */
    @JsonProperty("Delivery2")
    private String delivery2;

    /**
     * Delivery3
     */
    @JsonProperty("Delivery3")
    private String delivery3;

    /**
     * Delivery4
     */
    @JsonProperty("Delivery4")
    private String delivery4;

    /**
     * Delivery5
     */
    @JsonProperty("Delivery5")
    private String delivery5;

    /**
     * Delivery6
     */
    @JsonProperty("Delivery6")
    private String delivery6;

    /**
     * Delivery7
     */
    @JsonProperty("Delivery7")
    private String delivery7;

    /**
     * SpecialCashCheck
     */
    @JsonProperty("SpecialCashCheck")
    private int specialCashCheck;

    /**
     * Warehouse
     */
    @JsonProperty("Warehouse")
    private String warehouse;
}
