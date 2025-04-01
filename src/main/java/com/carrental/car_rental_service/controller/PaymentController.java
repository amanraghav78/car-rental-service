package com.carrental.car_rental_service.controller;

import com.carrental.car_rental_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{bookingId}")
    public ResponseEntity<String> payForBooking(@PathVariable Long bookingId){
        String response = paymentService.processPayment(bookingId);
        return ResponseEntity.ok(response);
    }
}
