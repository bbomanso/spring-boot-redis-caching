package com.beatr.springbootrediscaching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringBootRedisCachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisCachingApplication.class, args);
    }

}
