package com.kidschool.serviceabsence.student;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", url = "${student.service.url}")
public interface StudentServiceClient {
    @GetMapping("/api/v1/students/{id}")
    StudentResponse getStudentById(@PathVariable("id") Long id);

}
