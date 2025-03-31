package com.carrental.car_rental_service.dto;

import com.sun.jdi.PrimitiveValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String carModel;
    private String carRegistrationNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
}
