package ru.random.walk.club_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.random.walk.config.StorageAutoConfiguration;

@SpringBootApplication
@Import(StorageAutoConfiguration.class)
public class ClubServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubServiceApplication.class, args);
    }

}
