package com.nowcoder.community.config;

import com.nowcoder.community.controller.intercepter.AlphaIntercepter;
import com.nowcoder.community.controller.intercepter.LoginRequiredIntercepter;
import com.nowcoder.community.controller.intercepter.LoginTicketInterception;
import com.nowcoder.community.controller.intercepter.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaIntercepter alphaIntercepter;

    @Autowired
    private LoginTicketInterception loginTicketInterception;

    @Autowired
    private LoginRequiredIntercepter loginRequiredIntercepter;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaIntercepter)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterception)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

//        registry.addInterceptor(loginRequiredIntercepter)
//                .excludePathPatterns("/**/*.css");

        registry.addInterceptor(loginRequiredIntercepter)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
