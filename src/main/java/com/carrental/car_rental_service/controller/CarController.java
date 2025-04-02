package com.carrental.car_rental_service.controller;

import com.azure.core.annotation.Get;
import com.carrental.car_rental_service.dto.CarRequest;
import com.carrental.car_rental_service.dto.CarResponse;
import com.carrental.car_rental_service.entity.Car;
import com.carrental.car_rental_service.repository.CarRepository;
import com.carrental.car_rental_service.service.CarService;
import com.carrental.car_rental_service.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private CarService carService;
    private final CarRepository carRepository;
    private final PricingService pricingService;

    @PreAuthorize("hasRole('ADMIN)")
    @PostMapping
    public ResponseEntity<CarResponse> addCar(@RequestBody CarRequest carRequest){
        return new ResponseEntity<>(carService.addCar(carRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars(){
        return ResponseEntity.ok(carService.getAllCars());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @RequestBody CarRequest request
    ){
        return ResponseEntity.ok(carService.updateCar(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id){
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Car> getAvailableCars(){
        List<Car> cars = carRepository.findAvailableCars();
        for(Car car: cars){
            car.setCurrentPrice(pricingService.calculateDynamicPrice(car));
        }
        return cars;
    }
}
