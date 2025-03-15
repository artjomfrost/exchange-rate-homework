import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { AppComponent } from './app.component';
import { ExchangeRateComponent } from './exchange-rate.component';
import { ExchangeRateService } from './services/exchange-rate.service';

@NgModule({
  declarations: [
    AppComponent,
    ExchangeRateComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    CommonModule,
    NgxChartsModule
  ],
  providers: [ExchangeRateService],
  bootstrap: [AppComponent],
})
export class AppModule {}
