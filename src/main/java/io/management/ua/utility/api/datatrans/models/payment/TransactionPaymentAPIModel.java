package io.management.ua.utility.api.datatrans.models.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.management.ua.utility.api.datatrans.models.TransactionProcessingError;
import lombok.Data;

import java.io.Serializable;

@Data
@JacksonXmlRootElement(localName = "paymentService")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionPaymentAPIModel implements Serializable {
    @JacksonXmlProperty(isAttribute = true)
    private String version;
    @JacksonXmlProperty(localName = "body")
    private PaymentBody paymentBody;
    @JacksonXmlProperty
    private TransactionProcessingError error;
}

/**
 * API Documentation Link https://docs.datatrans.ch/v1.0.1/docs/google-pay
 */