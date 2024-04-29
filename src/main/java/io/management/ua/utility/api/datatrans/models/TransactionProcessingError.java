package io.management.ua.utility.api.datatrans.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionProcessingError implements Serializable {
    @JacksonXmlProperty(localName = "errorCode")
    private int errorCode;
    @JacksonXmlProperty(localName = "errorMessage")
    private String errorMessage;
    @JacksonXmlProperty(localName = "errorDetail")
    private String errorDetail;
}