package io.management.ua.utility.api.nova.post.models.http.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class GetDocumentPriceResponse {
    /**
     * Оціночна вартість
     */
    @JsonProperty("AssessedCost")
    private Long assessedCost;
    /**
     * Вартість зворотної доставки
     */
    @JsonProperty("CostRedelivery")
    private Long costRedelivery;
    /**
     * Вартість пакування
     */
    @JsonProperty("CostPack")
    private Long costPack;
    /**
     * Вартість
     */
    @JsonProperty("Cost")
    private Long cost;
}
