package com.carrental.car_rental_service.repository;

import com.carrental.car_rental_service.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
}
