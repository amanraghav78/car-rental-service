package com.carrental.car_rental_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String couponCode;
}
