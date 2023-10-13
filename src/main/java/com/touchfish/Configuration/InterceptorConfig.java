package com.touchfish.Configuration;

import com.touchfish.Tool.MethodHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public MethodHandler getMethodHandler(){
        return new MethodHandler();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getMethodHandler()).addPathPatterns("/**");
    }
}
