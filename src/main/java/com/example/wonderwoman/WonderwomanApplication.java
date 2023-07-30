package com.example.wonderwoman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WonderwomanApplication {

    public static void main(String[] args) {
        SpringApplication.run(WonderwomanApplication.class, args);
    }

}
