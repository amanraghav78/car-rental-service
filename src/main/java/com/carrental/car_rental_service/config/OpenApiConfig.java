package com.carrental.car_rental_service.config;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI carRentalOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Car Rental Service API")
                        .description("APIs for managing cars, bookings, and users")
                        .version("v1.0"));
    }

    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("car-rental")
                .pathsToMatch("/api/**")
                .build();
    }
}
