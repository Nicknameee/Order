package io.management.ua.utility.api.datatrans.models.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionAuthorizationXmlModel {
    @JacksonXmlProperty(isAttribute = true, localName =  "refno")
    private String refNumber;
    @JacksonXmlProperty(isAttribute = true)
    private String trxStatus;
    @JacksonXmlProperty(localName = "request")
    private AuthorizationRequest authorizationRequest;
    @JacksonXmlProperty(localName = "response")
    private AuthorizationResponse authorizationResponse;
}
