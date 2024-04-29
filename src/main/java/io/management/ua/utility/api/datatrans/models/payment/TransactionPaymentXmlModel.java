package io.management.ua.utility.api.datatrans.models.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionPaymentXmlModel {
    @JacksonXmlProperty(isAttribute = true, localName =  "refno")
    private String refNumber;
    @JacksonXmlProperty(isAttribute = true)
    private String trxStatus;
    @JacksonXmlProperty(localName = "request")
    private PaymentRequest paymentRequest;
    @JacksonXmlProperty(localName = "response")
    private PaymentResponse paymentResponse;
}
