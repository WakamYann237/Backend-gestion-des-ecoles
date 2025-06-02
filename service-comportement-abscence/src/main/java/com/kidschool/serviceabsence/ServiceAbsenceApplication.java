package com.kidschool.serviceabsence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ServiceAbsenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAbsenceApplication.class, args);
    }

}
