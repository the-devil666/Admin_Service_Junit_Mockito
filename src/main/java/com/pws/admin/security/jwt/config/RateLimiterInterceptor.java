//package com.pws.admin.security.jwt.config;
//
//import io.github.resilience4j.ratelimiter.RateLimiter;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class RateLimiterInterceptor extends HandlerInterceptorAdapter {
//
//    private RateLimiter rateLimiter = RateLimiter.create(10.0); // 10 requests per second
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (rateLimiter.tryAcquire()) {
//            return true;
//        } else {
//            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            response.getWriter().write("Rate limit exceeded");
//            return false;
//        }
//    }
//}
