package com.carrental.car_rental_service.exception;

public class InvalidBookingException extends RuntimeException{
    public InvalidBookingException(String message){
        super(message);
    }
}
