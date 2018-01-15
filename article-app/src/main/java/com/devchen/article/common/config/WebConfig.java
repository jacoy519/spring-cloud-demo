package com.devchen.article.common.config;

import com.devchen.article.common.interceptor.UnloginAccountInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        UnloginAccountInterceptor unloginAccountInterceptor = new UnloginAccountInterceptor();
        registry.addInterceptor(unloginAccountInterceptor)
                .addPathPatterns("/article/create-new-article/**");
    }

}
