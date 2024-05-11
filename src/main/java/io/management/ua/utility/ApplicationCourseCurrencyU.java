package io.management.ua.utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApplicationCourseCurrencyU {
    @JsonProperty("ccy")
    private String currency;
    private Float buy;
    private Float sale;
}
