package com.carrental.car_rental_service.exception;

public class PaymentFailedException extends RuntimeException{
    public PaymentFailedException(String message){
        super(message);
    }
}
