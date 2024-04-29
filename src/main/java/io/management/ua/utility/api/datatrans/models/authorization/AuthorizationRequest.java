package io.management.ua.utility.api.datatrans.models.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationRequest {
    @JacksonXmlCData
    @JacksonXmlProperty
    private String googlePayData;
    private String sign;
    private BigDecimal amount;
    private String currency;
    @JacksonXmlProperty(localName = "reqtype")
    private String requestType;
}
