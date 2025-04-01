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
    private final EmailService emailService;
    private final Random random = new Random();

    public String processPayment(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.PAID){
            return "Booking already paid";
        }

        boolean isSuccessful = random.nextBoolean();

        String emailBody;
        if (isSuccessful){
            booking.setPaymentStatus(PaymentStatus.PAID);
            emailBody = "<h3>Payment Successful</h3>"
                    + "<p>Dear " + booking.getUser().getName() + ",</p>"
                    + "<p>Your payment for the car rental has been successfully processed.</p>"
                    + "<ul>"
                    + "<li><b>Car:</b> " + booking.getCar().getModel() + "</li>"
                    + "<li><b>Booking ID:</b> " + booking.getId() + "</li>"
                    + "<li><b>Amount Paid:</b> $" + booking.getTotalPrice() + "</li>"
                    + "</ul>"
                    + "<p>Enjoy your ride!</p>";
            emailService.sendEmail(booking.getUser().getEmail(), "Car Rental Payment Succesful", emailBody);

            bookingRepository.save(booking);
            return "Payment successful!";
        } else {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            emailBody = "<h3>Payment Failed</h3>"
                    + "<p>Dear " + booking.getUser().getName() + ",</p>"
                    + "<p>Your payment for the car rental failed. Please try again.</p>"
                    + "<p>Booking ID: " + booking.getId() + "</p>";

            emailService.sendEmail(booking.getUser().getEmail(), "Car Rental Payment Failed", emailBody);
            bookingRepository.save(booking);
            return "Payment failed. Please retry";
        }
    }
}
