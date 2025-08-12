package com.fundicion.lara.config;

import com.fundicion.lara.config.security.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {

    private final Interceptor interceptor;


    @Autowired
    public WebMvcConfig(@Lazy Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.interceptor);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/swagger-ui.html");
    }
}
