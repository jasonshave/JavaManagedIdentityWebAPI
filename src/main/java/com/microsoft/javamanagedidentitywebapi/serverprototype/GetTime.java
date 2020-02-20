package com.microsoft.javamanagedidentitywebapi.serverprototype;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalTime;

@RestController
@RequestMapping("/gettime")
public class GetTime {

    @GetMapping
    @PostMapping
    public String getTime(){
        return LocalTime.now().toString();
    }
}
