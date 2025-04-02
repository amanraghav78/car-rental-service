package com.carrental.car_rental_service.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ApiLoggingAspect {

    @Around("execution(* com.carrental.controller..*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attributes != null) ? attributes.getRequest() : null;

        String requestUrl = (request != null) ? request.getRequestURI() : "Unknown";
        String method = ( request != null) ? request.getMethod() : "Unknown";
        String clientIp = ( request != null) ? request.getRemoteAddr() : "Unknown";

        String user = "Anonymous";
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            user = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        log.info("Incoming request: [{}] {} | User: {} | IP: {}", method, requestUrl, user, clientIp);

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        int statusCode = (result instanceof ResponseEntity<?>) ?
                ((ResponseEntity<?>) result).getStatusCode().value() : 200;

        log.info("Completed Request: [{}]{} | Status: {} | Time: {}ms", method, requestUrl, statusCode, executionTime);

        return result;

    }

}
