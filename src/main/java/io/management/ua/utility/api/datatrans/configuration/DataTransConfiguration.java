package io.management.ua.utility.api.datatrans.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;

@Component
@Data
@ConfigurationProperties(prefix = "api.payment.data-trans")
public class DataTransConfiguration {
    private String merchantId;
    private String sign;
    @Pattern(regexp = "^(USD|EUR|CHF)$")
    private String acquiringCurrency;
    private String password;
    private ApiProperties authorization;
    private ApiProperties payment;


    @Data
    public static class ApiProperties {
        private String apiUrl;
        private String apiVersion;
    }
}
