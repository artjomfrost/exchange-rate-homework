import { Component, OnInit } from '@angular/core';
import { ExchangeRateService } from './services/exchange-rate.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

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

  amount: number = 0;
  fromCurrency: string = '';
  toCurrency: string = ''; 
  convertedAmount: number = 0;
  exchangeRate: number = 0;

  constructor(private exchangeRateService: ExchangeRateService, private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchCurrencies();
  }

  fetchCurrencies(): void {
    this.exchangeRateService.getCurrencies().subscribe(
      (data: string[]) => { 
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
        this.exchangeRateHistory = data;
      },
      (error: any) => {
        console.error('Error fetching exchange rate history', error);
      }
    );
  }

  convertCurrency(): void {
    if (!this.amount || !this.fromCurrency || !this.toCurrency) {
      alert("Please enter the sum and choose a currency!");
      return;
    }
  
    this.http.get<any>(`http://localhost:8080/api/convert`, {
      params: {
        amount: this.amount.toString(),
        fromCurrency: this.fromCurrency,
        toCurrency: this.toCurrency
      }
    }).subscribe(response => {
      this.convertedAmount = response.convertedAmount;
      this.exchangeRate = response.exchangeRate;
    }, error => {
      console.error("Exchange error", error);
    });
  }
}
