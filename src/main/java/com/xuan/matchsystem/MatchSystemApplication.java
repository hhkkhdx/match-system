package com.xuan.matchsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.xuan.matchsystem.mapper")
@EnableScheduling
public class MatchSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchSystemApplication.class, args);
    }

}
