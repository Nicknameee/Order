package io.management.ua.utility.api.datatrans.models.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.management.ua.utility.api.datatrans.models.TransactionProcessingError;
import lombok.Data;

import java.io.Serializable;

@Data
@JacksonXmlRootElement(localName = "authorizationService")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionAuthorizationAPIModel implements Serializable {
    @JacksonXmlProperty(isAttribute = true)
    private String version;
    @JacksonXmlProperty(localName = "body")
    private AuthorizationBody authorizationBody;
    @JacksonXmlProperty
    private TransactionProcessingError error;
}

/**
 * API Documentation Link https://docs.datatrans.ch/v1.0.1/docs/google-pay
 */