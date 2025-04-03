package com.carrental.car_rental_service.controller;

import com.carrental.car_rental_service.dto.BookingRequest;
import com.carrental.car_rental_service.dto.BookingResponse;
import com.carrental.car_rental_service.dto.PriceBreakdownDTO;
import com.carrental.car_rental_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasRole('USER)")
    @PostMapping
    public ResponseEntity<BookingResponse> bookCar(@RequestBody BookingRequest request){
        return new ResponseEntity<>(bookingService.bookCar(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings(){
        return ResponseEntity.ok(bookingService.getUserBookings());
    }

    @PreAuthorize("hasRole('USER)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id){
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/price-breakdown")
    public PriceBreakdownDTO getPriceBrreakdown(@RequestBody BookingRequest bookingRequest){
        return bookingService.getPriceBreakdown(bookingRequest);
    }
}
