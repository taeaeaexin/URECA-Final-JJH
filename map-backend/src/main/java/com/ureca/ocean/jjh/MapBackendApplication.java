package com.ureca.ocean.jjh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
//@EnableFeignClients(basePackages = "com.ureca.ocean.jjh.mapbackend.user.client")  // FeignClient 관련 설정
public class MapBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapBackendApplication.class, args);
    }

}
