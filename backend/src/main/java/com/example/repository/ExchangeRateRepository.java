package com.example.repository;

import com.example.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrencyAndDate(String currency, LocalDate date);
    List<ExchangeRate> findByDate(LocalDate date);
    List<ExchangeRate> findByCurrency(String currency);
    List<ExchangeRate> findByCurrencyOrderByDateDesc(String currency);

    @Query("SELECT DISTINCT e.currency FROM ExchangeRate e")
    List<String> getDistinctCurrencies();
}
