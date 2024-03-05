package io.management.ua.utility.api.nova.post.models.http.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class GetCityRefResponse {
    /**
     * Ідентифікатор міста
     */
    @JsonProperty("Ref")
    private String ref;
}
