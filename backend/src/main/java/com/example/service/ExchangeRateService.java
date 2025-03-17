package com.example.service;

import com.example.model.ExchangeRate;
import com.example.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private final ExchangeRateRepository exchangeRateRepository;
    private static final String ECB_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public Map<String, Double> getExchangeRates() {
        String xmlData = fetchExchangeRates();
        return parseXML(xmlData);
    }

    public String fetchExchangeRates() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(ECB_URL, String.class);
    }

    public Map<String, Double> parseXML(String xmlData) {
        Map<String, Double> exchangeRates = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8)));

            NodeList cubes = document.getElementsByTagName("Cube");
            exchangeRates.put("EUR", 1.0); // Евро - базовая валюта

            for (int i = 0; i < cubes.getLength(); i++) {
                Element element = (Element) cubes.item(i);
                if (element.hasAttribute("currency") && element.hasAttribute("rate")) {
                    exchangeRates.put(element.getAttribute("currency"), Double.parseDouble(element.getAttribute("rate")));
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing XML: {}", e.getMessage());
        }
        return exchangeRates;
    }

    @PostConstruct
    public void populateHistoricalData() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(90);

        for (LocalDate date = startDate; date.isBefore(today); date = date.plusDays(1)) {
            if (exchangeRateRepository.findByDate(date).isEmpty()) {
                saveExchangeRatesForDate(date);
            }
        }
    }

    public void saveExchangeRatesForDate(LocalDate date) {
        logger.info("Saving exchange rates for {}", date);
        Map<String, Double> rates = parseXML(fetchExchangeRates());

        rates.forEach((currency, rate) -> {
            if (exchangeRateRepository.findByCurrencyAndDate(currency, date).isEmpty()) {
                exchangeRateRepository.save(new ExchangeRate(currency, rate, date));
                logger.info("Saved: {} = {} (Date: {})", currency, rate, date);
            }
        });
    }

    public List<Map<String, Object>> getHistory(String currency) {
        return exchangeRateRepository.findByCurrencyOrderByDateDesc(currency)
                .stream()
                .map(rate -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", rate.getDate().toString());
                    map.put("rate", rate.getRate());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public Set<String> getAvailableCurrencies() {
        try {
            List<String> currencies = exchangeRateRepository.getDistinctCurrencies();
            logger.info("Fetched currencies from DB: {}", currencies);

            if (currencies.isEmpty()) {
                logger.warn("No currencies found in the database.");
                throw new RuntimeException("No currencies found.");
            }
            return new HashSet<>(currencies);
        } catch (Exception e) {
            logger.error("Error fetching currencies: {}", e.getMessage());
            throw new RuntimeException("Error fetching currencies: " + e.getMessage());
        }
    }

    public Map<String, Object> convertCurrency(double amount, String fromCurrency, String toCurrency) {
        Map<String, Double> rates = parseXML(fetchExchangeRates());

        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            logger.warn("Invalid currency conversion request: {} to {}", fromCurrency, toCurrency);
            return Map.of(
                    "error", "Invalid currency provided",
                    "fromCurrency", fromCurrency,
                    "toCurrency", toCurrency
            );
        }

        double exchangeRate = rates.get(toCurrency) / rates.get(fromCurrency);
        double convertedAmount = amount * exchangeRate;

        return Map.of(
                "fromCurrency", fromCurrency,
                "toCurrency", toCurrency,
                "amount", amount,
                "convertedAmount", convertedAmount,
                "exchangeRate", exchangeRate
        );
    }
}
