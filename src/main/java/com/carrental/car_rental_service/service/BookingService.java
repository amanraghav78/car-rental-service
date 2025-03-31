package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.dto.BookingRequest;
import com.carrental.car_rental_service.dto.BookingResponse;
import com.carrental.car_rental_service.entity.Booking;
import com.carrental.car_rental_service.entity.Car;
import com.carrental.car_rental_service.entity.User;
import com.carrental.car_rental_service.repository.BookingRepository;
import com.carrental.car_rental_service.repository.CarRepository;
import com.carrental.car_rental_service.repository.UserRepository;
import io.netty.channel.local.LocalAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private BookingRepository bookingRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;

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

            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Booking booking = Booking.builder()
                    .startDate(bookingRequest.getStartDate())
                    .endDate(bookingRequest.getEndDate())
                    .totalPrice(totalPrice)
                    .car(car)
                    .user(user)
                    .build();

            Booking savedBooking = bookingRepository.save(booking);

            return new BookingResponse(
                    savedBooking.getId(),
                    car.getModel(),
                    car.getRegistrationNumber(),
                    savedBooking.getStartDate(),
                    savedBooking.getEndDate(),
                    savedBooking.getTotalPrice()
            );
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

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2){
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
}
