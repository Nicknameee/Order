package io.management.ua.utility.api.datatrans.models.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    private String responseCode;
    private String responseMessage;
}
