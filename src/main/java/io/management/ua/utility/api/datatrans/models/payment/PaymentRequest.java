package io.management.ua.utility.api.datatrans.models.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest {
    @JacksonXmlProperty(localName = "uppTransactionId")
    private String transactionId;
    private String sign;
    private BigDecimal amount;
    private String currency;
    @JacksonXmlProperty(localName = "reqtype")
    private String requestType;
}
