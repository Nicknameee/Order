package io.management.ua;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Order {
    public static void main(String[] args) {
        log.debug("Order service started");
        SpringApplication.run(Order.class);
    }
}
