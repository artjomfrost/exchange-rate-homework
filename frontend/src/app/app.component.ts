import { Component } from '@angular/core';
import { ExchangeRateComponent } from './exchange-rate.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ExchangeRateComponent],
  template: '<app-exchange-rate></app-exchange-rate>'
})
export class AppComponent {
  title = 'Exchange Rate Portal';
}
