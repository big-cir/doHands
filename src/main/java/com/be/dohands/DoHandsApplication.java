package com.be.dohands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DoHandsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoHandsApplication.class, args);
    }

}
