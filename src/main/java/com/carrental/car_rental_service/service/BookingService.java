package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.dto.BookingRequest;
import com.carrental.car_rental_service.dto.BookingResponse;
import com.carrental.car_rental_service.dto.PriceBreakdownDTO;
import com.carrental.car_rental_service.entity.*;
import com.carrental.car_rental_service.repository.BookingRepository;
import com.carrental.car_rental_service.repository.CarRepository;
import com.carrental.car_rental_service.repository.CouponRepository;
import com.carrental.car_rental_service.repository.UserRepository;
import io.netty.channel.local.LocalAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private BookingRepository bookingRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private CouponRepository couponRepository;
    private final PricingService pricingService;

    public BookingResponse bookCar(BookingRequest bookingRequest){
        Car car = carRepository.findById(bookingRequest.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if(!car.isAvailable()){
            throw new RuntimeException("Car is not available");
        }

        List<Booking> existing = bookingRepository.findAll();

        for(Booking b : existing) {
            if (Objects.equals(b.getCar().getId(), car.getId())) {
                if (datesOverlap(bookingRequest.getStartDate(), bookingRequest.getEndDate(), b.getStartDate(), b.getEndDate())) {
                    throw new RuntimeException("Car is already booked for selected dates");
                }
            }
        }

            long days = bookingRequest.getEndDate().toEpochDay() - bookingRequest.getStartDate().toEpochDay();

            if(days <= 0) throw new RuntimeException("End date must be after start date");

            double totalPrice = days * car.getRentalPricePerDay();

        Coupon appliedCoupon = null;

        if(bookingRequest.getCouponCode() != null){
            appliedCoupon = couponRepository.findByCode(bookingRequest.getCouponCode())
                    .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

            if(appliedCoupon.getExpiryDate().isBefore(LocalDate.now())){
                throw new RuntimeException("Coupon has expired");
            }

            if(appliedCoupon.getCurrentUsage() >= appliedCoupon.getMaxUsage()){
                throw new RuntimeException("Coupon usage limit reached");
            }

            if(appliedCoupon.isPercentage()){
                totalPrice -= (totalPrice * appliedCoupon.getDiscount() / 100);
            } else {
                totalPrice -= appliedCoupon.getDiscount();
            }

            appliedCoupon.setCurrentUsage(appliedCoupon.getCurrentUsage() + 1);
            couponRepository.save(appliedCoupon);
        }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Booking booking = Booking.builder()
                    .startDate(bookingRequest.getStartDate())
                    .endDate(bookingRequest.getEndDate())
                    .totalPrice(totalPrice)
                    .paymentStatus(PaymentStatus.PENDING)
                    .car(car)
                    .user(user)
                    .build();

            Booking savedBooking = bookingRepository.save(booking);

            String emailBody = "<h3>Booking Confirmed</h3>"
                    + "<p>Dear " + user.getName() + ",</p>"
                    + "<p>Your car rental booking has been confirmed:</p>"
                    + "<ul>"
                    + "<li><b>Car:</b> " + car.getModel() + "</li>"
                    + "<li><b>From:</b> " + bookingRequest.getStartDate() + "</li>"
                    + "<li><b>To:</b> " + bookingRequest.getEndDate() + "</li>"
                    + "<li><b>Total Price:</b> $" + totalPrice + "</li>"
                    + "</ul>"
                    + "<p>Thank you for choosing our service!</p>";

            emailService.sendEmail(user.getEmail(), "Car Rental Booking confirmation", emailBody);

            return new BookingResponse(
                    savedBooking.getId(),
                    car.getModel(),
                    car.getRegistrationNumber(),
                    savedBooking.getStartDate(),
                    savedBooking.getEndDate(),
                    savedBooking.getTotalPrice()
            );
    }

    public PriceBreakdownDTO getPriceBreakdown(BookingRequest bookingRequest){
        Car car = carRepository.findById(bookingRequest.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        long rentalDays = ChronoUnit.DAYS.between(bookingRequest.getStartDate(), bookingRequest.getEndDate());

        if(rentalDays < 1) {
            throw new IllegalArgumentException("Rental days must be atleast 1 day");
        }

        double basePricePerDay = car.getRentalPricePerDay();
        double dynamicPricePerDay = pricingService.calculateDynamicPrice(car);
        double totalBasePrice = basePricePerDay * rentalDays;
        double totalDynamicPrice = dynamicPricePerDay * rentalDays;

        double discount = applyDiscount(bookingRequest.getCouponCode(), totalDynamicPrice);
        double tax = totalDynamicPrice * 0.10;
        double finalPrice = totalBasePrice - discount + tax;

        return new PriceBreakdownDTO(
                rentalDays,
                basePricePerDay,
                dynamicPricePerDay,
                totalBasePrice,
                totalDynamicPrice,
                discount,
                tax,
                finalPrice
        );
    }

    private double applyDiscount(String couponCode, double amount){
        if("Welcome10".equalsIgnoreCase(couponCode)){
            return amount * 0.10;
        }
        return 0.0;
    }

    public List<BookingResponse> getUserBookings(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUser(user).stream()
                .map(b -> new BookingResponse(
                        b.getId(),
                        b.getCar().getModel(),
                        b.getCar().getRegistrationNumber(),
                        b.getStartDate(),
                        b.getEndDate(),
                        b.getTotalPrice()
                ))
                .collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if(!booking.getUser().getId().equals(user.getId())){
            throw new RuntimeException("You are not authorised to cancel the bookibng");
        }

        bookingRepository.delete(booking);
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2){
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
}
