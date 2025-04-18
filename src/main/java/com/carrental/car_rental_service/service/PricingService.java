package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.entity.Car;
import com.carrental.car_rental_service.repository.BookingRepository;
import com.carrental.car_rental_service.repository.CarRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class PricingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public double calculateDynamicPrice(Car car){
        String key = "car_price:" + car.getId();

        Object cachedPrice = redisTemplate.opsForValue().get(key);
        if(cachedPrice != null){
            return Double.parseDouble(cachedPrice.toString());
        }

        double basePrice = car.getCurrentPrice();
        double demandFactor = getDemandFactor();
        double availabilityFactor = getAvailabilityFactor(car);
        double seasonFactor = getSeasonFactor();

        double finalPrice = basePrice * demandFactor * availabilityFactor * seasonFactor;

        redisTemplate.opsForValue().set(key, finalPrice, 10, TimeUnit.MINUTES);
        return Math.round(finalPrice * 100.0)/100.0;
    }

    public double getDemandFactor(){
        int currentBookings = bookingRepository.countBookingsToday();
        if(currentBookings > 50) return 1.5;
        if(currentBookings > 30) return 1.2;
        return 1.0;
    }

    private double getAvailabilityFactor(Car car){
        long totalCars = carRepository.count();
        long availableCars = carRepository.countAvailableCars();
        double availabilityRatio = (double) availableCars /totalCars;

        if(availabilityRatio < 0.2) return 1.5;
        if(availabilityRatio < 0.5) return  1.2;
        return 1.0;
    }

    private double getSeasonFactor(){
        LocalDate today = LocalDate.now();
        Month month = today.getMonth();

        if(month == Month.DECEMBER || month == Month.JULY) return 1.5;
        if(month == Month.MAY || month == Month.OCTOBER) return 1.2;
        return 1.0;
    }
}
