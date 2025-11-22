package com.arkaback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ArkaBackHexaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArkaBackHexaApplication.class, args);
    }

}
