package com.carrental.car_rental_service.controller;

import com.carrental.car_rental_service.dto.CarRequest;
import com.carrental.car_rental_service.dto.CarResponse;
import com.carrental.car_rental_service.service.CarService;
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
}
