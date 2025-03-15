package com.example.controller;

import com.example.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ExchangeRateController {
    
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/currencies")
    public Set<String> getCurrencies() {
        return exchangeRateService.getAvailableCurrencies();
    }

    @GetMapping("/exchange-rates")
    public Map<String, Double> getExchangeRates() {
        return exchangeRateService.getExchangeRates();
    }

    @GetMapping("/test-xml")
    public String testXmlFetch() {
        return exchangeRateService.fetchExchangeRates();
    }

    @GetMapping("/exchange-rates/{currency}/history")
    public List<Map<String, Object>> getHistory(@PathVariable String currency) {
        return exchangeRateService.getHistory(currency);
    }
}
