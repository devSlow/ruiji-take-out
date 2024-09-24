package com.slow.ruijitakeout.config;

import com.slow.ruijitakeout.common.JacksonObjectMapper;
import com.slow.ruijitakeout.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class InterceptorConfig {

    @Autowired
    private LoginInterceptor loginInterceptor;
    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }
    @Bean
    public WebMvcConfigurerAdapter LoginInterceptorConfig(){
        return new WebMvcConfigurerAdapter(){
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(loginInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns("/employee/login",
                            "/employee/logout",
                            "/backend/**",
                            "/front/**",
                            "/common/**",
                            "/user/sendMsg",
                            "/user/login");
            }
            /**
             * 处理静态资源映射
             * @param registry
             */
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
                registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
            }

            /**
             * 自定义消息转换器
             * @param converters
             */
            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//                创建消息转换器对象
                MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//                设置对象映射器
                messageConverter.setObjectMapper(new JacksonObjectMapper());
                converters.add(0,messageConverter);
            }
        };
    }

}
