package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.entity.Coupon;
import com.carrental.car_rental_service.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon){
        return couponRepository.save(coupon);
    }
}
