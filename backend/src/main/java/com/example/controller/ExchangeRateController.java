package com.example.controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ExchangeRateController {

    @GetMapping("/currencies")
    public List<String> getCurrencies() {
        return List.of("USD", "EUR", "GBP", "JPY");
    }
}
