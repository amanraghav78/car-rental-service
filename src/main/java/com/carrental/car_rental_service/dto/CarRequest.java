package com.carrental.car_rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRequest {
    private String brand;
    private String model;
    private String registrationNumber;
    private double rentalPricePerDay;
}
