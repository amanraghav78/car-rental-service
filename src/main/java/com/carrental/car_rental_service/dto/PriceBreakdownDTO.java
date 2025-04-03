package com.carrental.car_rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PriceBreakdownDTO {
    private long rentalDays;
    private double basePricePerDay;
    private double dynamicPricePerDay;
    private double totalBasePrice;
    private double totalDynamicPrice;
    private double discount;
    private double tax;
    private double finalPrice;
}
