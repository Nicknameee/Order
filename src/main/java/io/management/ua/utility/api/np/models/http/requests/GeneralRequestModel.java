package io.management.ua.utility.api.np.models.http.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralRequestModel<T> {
    private String apiKey;
    private String calledMethod;
    private String modelName;
    private T methodProperties;

    public GeneralRequestModel(String apiKey) {
        this.apiKey = apiKey;
    }

    public GeneralRequestModel(String apiKey, String calledMethod) {
        this.apiKey = apiKey;
        this.calledMethod = calledMethod;
    }
    public GeneralRequestModel(String apiKey, String calledMethod, String modelName) {
        this.apiKey = apiKey;
        this.calledMethod = calledMethod;
        this.modelName = modelName;
    }
}
