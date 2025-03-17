package com.example.jobs;

import com.example.service.ExchangeRateService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class ExchangeRateUpdateJob implements Job {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateUpdateJob(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        LocalDate today = LocalDate.now();
        exchangeRateService.saveExchangeRatesForDate(today);
    }
}