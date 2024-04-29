package io.management.ua.utility.api.datatrans.models.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentBody implements Serializable {
    @JacksonXmlProperty(isAttribute = true)
    private String merchantId;
    @JacksonXmlProperty(isAttribute = true)
    private String status;
    @JacksonXmlProperty
    private TransactionPaymentXmlModel transaction;
}
