package io.management.ua.utility.api.datatrans.models.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationBody implements Serializable {
    @JacksonXmlProperty(isAttribute = true)
    private String merchantId;
    @JacksonXmlProperty(isAttribute = true)
    private String status;
    @JacksonXmlProperty
    private TransactionAuthorizationXmlModel transaction;
}
