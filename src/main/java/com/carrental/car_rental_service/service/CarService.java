package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.dto.CarRequest;
import com.carrental.car_rental_service.dto.CarResponse;
import com.carrental.car_rental_service.entity.Car;
import com.carrental.car_rental_service.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public CarResponse addCar(CarRequest request){
        if(carRepository.existsByRegistrationNumber(request.getRegistrationNumber())){
            throw new RuntimeException("Car with the registration already exists");
        }

        Car car = Car.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .registrationNumber(request.getRegistrationNumber())
                .rentalPricePerDay(request.getRentalPricePerDay())
                .available(true)
                .build();

        Car savedCar = carRepository.save(car);
        return mapToResponse(savedCar);
    }

    public List<CarResponse> getAllCars(){
        return carRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CarResponse updateCar(Long id, CarRequest request){
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setRegistrationNumber(request.getRegistrationNumber());
        car.setRentalPricePerDay(request.getRentalPricePerDay());

        return mapToResponse(carRepository.save(car));
    }

    public void deleteCar(Long id){
        if(carRepository.findById(id).isEmpty()){
            throw new RuntimeException("Car not found");
        }

        carRepository.deleteById(id);
    }

    private CarResponse mapToResponse(Car car){
        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getRegistrationNumber(),
                car.getRentalPricePerDay(),
                car.isAvailable()
        );
    }
}
