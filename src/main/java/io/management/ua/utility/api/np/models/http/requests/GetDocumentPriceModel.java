package io.management.ua.utility.api.np.models.http.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetDocumentPriceModel implements Serializable {
    /**
     * Ідентифікатор (REF) міста відправника
     */
    @JsonProperty("CitySender")
    private String citySender;
    /**
     * Ідентифікатор (REF) міста отримувача
     */
    @JsonProperty("CityRecipient")
    private String cityRecipient;
    /**
     * Фактична вага, min - 0,1
     */
    @JsonProperty("Weight")
    private BigDecimal weight;
    @JsonProperty("ServiceType")
    private String serviceType;
    /**
     * Оціночна вартість, ціле число (якщо не зазначити вартість то АРІ автоматично проставить мінімальну оціночну вартість =300.00)
     */
    @JsonProperty("Cost")
    private BigDecimal cost;
    /**
     * Тип вантажу: Cargo, Documents, TiresWheels, Pallet
     */
    @JsonProperty("CargoType")
    private String cargoType;
    /**
     * Кількість місць відправлення, ціле число
     */
    @JsonProperty("SeatsAmount")
    private String seatsAmount;
}
