package io.management.ua.utility.api.np.models.http.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetWarehousesModel {
    @JsonProperty("Page")
    private String page;
    @JsonProperty("CityName")
    private String cityName;
    @JsonProperty("FindByString")
    private String findByString;
    @JsonProperty("Limit")
    private String limit;
}
