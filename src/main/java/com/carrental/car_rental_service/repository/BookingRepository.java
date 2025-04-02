package com.carrental.car_rental_service.repository;

import com.carrental.car_rental_service.entity.Booking;
import com.carrental.car_rental_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    int countBookingsToday();
}
