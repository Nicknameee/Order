package io.management.ua.utility.api.datatrans.models.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationResponse {
    private String responseCode;
    private String responseMessage;
    private String uppTransactionId;
    private String authorizationCode;
    private String acqAuthorizationCode;
    private String returnCustomerCountry;
    private String aliasCC;
    @JacksonXmlProperty(localName = "expy")
    private int expirationYear;
    @JacksonXmlProperty(localName = "expm")
    private int expirationMonth;
    @JacksonXmlProperty(localName = "maskedCC")
    private String pan;
}
