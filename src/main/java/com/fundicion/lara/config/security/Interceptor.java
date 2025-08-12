package com.fundicion.lara.config.security;



import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Configuration
public class Interceptor implements HandlerInterceptor {

    private static final List<String> EXCLUDED_PATHS = List.of("favicon", "swagger", "api-docs");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isExcludedPath(request.getRequestURI())) return true;
        if ("OPTIONS".equals(request.getMethod())) return true;
        return true;
    }


    private boolean isExcludedPath(String uri) {
        return EXCLUDED_PATHS.stream().anyMatch(uri::contains);
    }
}

