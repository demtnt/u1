package com.example.u1;

import com.fasterxml.jackson.databind.Module;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@SpringBootApplication
public class NetworkDeploymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkDeploymentApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {};
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}