package com.carrental.car_rental_service.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.LogRecord;

@Component
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> rateLimitBuckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String clientIP = httpRequest.getRemoteAddr();

        Bucket bucket = rateLimitBuckets.computeIfAbsent(clientIP, key -> createNewBucket());

        if(bucket.tryConsume(1)){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            httpResponse.getWriter().write("429 - Too Many Requests. Please try again later.");
            httpResponse.setContentType("text/plain");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }

    }

    private Bucket createNewBucket(){
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build();
    }
}
