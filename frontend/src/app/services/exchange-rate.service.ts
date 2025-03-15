import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ExchangeRateService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getCurrencies(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/currencies`);
  }

  getExchangeRateHistory(currency: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/exchange-rates/${currency}/history`);
  }
}
