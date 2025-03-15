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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import java.util.stream.Collectors;


@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private final ExchangeRateRepository exchangeRateRepository;
    private static final String ECB_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public String fetchExchangeRates() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(ECB_URL, String.class);
    }

    public Map<String, Double> getExchangeRates() {
        String xmlData = fetchExchangeRates();
        return parseXML(xmlData);
    }

    private Map<String, Double> parseXML(String xmlData) {
        Map<String, Double> exchangeRates = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8)));

            NodeList cubes = document.getElementsByTagName("Cube");
            for (int i = 0; i < cubes.getLength(); i++) {
                Element element = (Element) cubes.item(i);
                String baseCurrency = "EUR";
                exchangeRates.put(baseCurrency, 1.0);
                
                if (element.hasAttribute("currency") && element.hasAttribute("rate")) {
                    String currency = element.getAttribute("currency");
                    Double rate = Double.parseDouble(element.getAttribute("rate"));
                    exchangeRates.put(currency, rate);
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
            if (exchangeRateRepository.findByCurrencyAndDate("USD", date).isEmpty()) {
                saveExchangeRatesForDate(date);
            }
        }
    }

    private void saveExchangeRatesForDate(LocalDate date) {
        logger.info("Saving rates per date {}", date);
        String xmlData = fetchExchangeRates();
        Map<String, Double> rates = parseXML(xmlData);

        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            ExchangeRate exchangeRate = new ExchangeRate(entry.getKey(), entry.getValue(), date);
            exchangeRateRepository.save(exchangeRate);
            logger.info("Saved: {} = {} (Date: {})", entry.getKey(), entry.getValue(), date);
        }
    }

    public List<Map<String, Object>> getHistory(String currency) {
        List<ExchangeRate> rates = exchangeRateRepository.findByCurrency(currency);
    
        return rates.stream()
            .map(rate -> Map.<String, Object>of( 
                "date", rate.getDate().toString(),
                "rate", rate.getRate()
            ))
            .collect(Collectors.toList());
    }

    public Set<String> getAvailableCurrencies() {
        Map<String, Double> exchangeRates = getExchangeRates();
        return exchangeRates.keySet();
    }

    
}
