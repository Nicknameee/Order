package io.management.ua;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = "io.management")
@Slf4j
@EnableEurekaClient
public class Order {
    public static void main(String[] args) {
        SpringApplication.run(Order.class);
        log.debug("Order service started");
    }
}
