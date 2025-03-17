package com.example.config;

import com.example.jobs.ExchangeRateUpdateJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail exchangeRateJobDetail() {
        return JobBuilder.newJob(ExchangeRateUpdateJob.class)
                .withIdentity("exchangeRateJob")
                .storeDurably()
                .build();
    }
    
    @Bean
    public Trigger exchangeRateTrigger(JobDetail exchangeRateJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(exchangeRateJobDetail)
                .withIdentity("exchangeRateTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(5)
                    .repeatForever())
                .build();
    }
}
