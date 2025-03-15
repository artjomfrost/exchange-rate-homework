import { Component, OnInit } from '@angular/core';
import { ExchangeRateService } from './services/exchange-rate.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-exchange-rate',
  templateUrl: './exchange-rate.component.html',
  styleUrls: ['./exchange-rate.component.css'],
  imports: [CommonModule, FormsModule]
})
export class ExchangeRateComponent implements OnInit {
  exchangeRateHistory: any[] = [];
  currencies: string[] = [];
  selectedCurrency: string = '';

  constructor(private exchangeRateService: ExchangeRateService) {}

  ngOnInit(): void {
    this.fetchCurrencies();
  }

  fetchCurrencies(): void {
    this.exchangeRateService.getCurrencies().subscribe(
      (data: string[]) => { 
        console.log('Currencies:', data);
        this.currencies = data;
      },
      (error: any) => {
        console.error('Error fetching currencies', error);
      }
    );
  }

  loadExchangeRateHistory(): void {
    if (!this.selectedCurrency) return;

    this.exchangeRateService.getExchangeRateHistory(this.selectedCurrency).subscribe(
      (data: any) => {
        console.log('Exchange Rate History:', data);
        this.exchangeRateHistory = data;
      },
      (error: any) => {
        console.error('Error fetching exchange rate history', error);
      }
    );
  }
}
