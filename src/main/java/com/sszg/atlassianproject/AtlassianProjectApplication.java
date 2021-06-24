package com.sszg.atlassianproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AtlassianProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtlassianProjectApplication.class, args);
        log.info("Starting Spring Boot App");
    }

}
