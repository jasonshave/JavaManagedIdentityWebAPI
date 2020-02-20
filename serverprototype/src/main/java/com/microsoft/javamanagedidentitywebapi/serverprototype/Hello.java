package com.microsoft.javamanagedidentitywebapi.serverprototype;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Hello implements CommandLineRunner {
    @Value("${applicationType}")
    private String applicationType;

    @Override
    public void run(String... args) {
        log.info("Application type is: " + applicationType);
    }
}
