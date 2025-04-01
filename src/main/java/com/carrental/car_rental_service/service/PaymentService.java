package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.entity.Booking;
import com.carrental.car_rental_service.entity.PaymentStatus;
import com.carrental.car_rental_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final Random random = new Random();

    public String processPayment(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.PAID){
            return "Booking already paid";
        }

        boolean isSuccessful = random.nextBoolean();

        if (isSuccessful){
            booking.setPaymentStatus(PaymentStatus.PAID);
            bookingRepository.save(booking);
            return "Payment successful!";
        } else {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);
            return "Payment failed. Please retry";
        }
    }
}
