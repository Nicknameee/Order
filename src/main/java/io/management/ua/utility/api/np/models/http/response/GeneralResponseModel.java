package io.management.ua.utility.api.np.models.http.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralResponseModel<T> implements Serializable {
    private boolean success;
    private List<T> data;
    private List<String> errors;
    private List<String> warnings;
}
