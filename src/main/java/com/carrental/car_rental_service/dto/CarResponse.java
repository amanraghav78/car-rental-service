package com.carrental.car_rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
    private Long id;
    private String model;
    private String registrationNumber;
    private double rentalPricePerDay;
    private boolean available;

    public CarResponse(Long id, String brand, String model, String registrationNumber, double rentalPricePerDay, boolean available) {
    }
}
