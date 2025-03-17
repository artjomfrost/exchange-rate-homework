# Currency Exchange Portal

### Description
A web application that provides **real-time exchange rates** and a **currency converter** based on the **European Central Bank (ECB)** data.

### Features:
 Automatically fetches and updates exchange rates daily (Quartz + Spring Boot)  
 Stores **90-day exchange rate history**  
 Allows users to convert currencies instantly  
 Simple and user-friendly **Angular UI**  

---

## Technologies Used
- **Backend**: Java, Spring Boot, Quartz
- **Frontend**: Angular
- **Database**: H2 (stored in `./data/exchange_rates`)
- **Data Source**: [ECB Exchange Rates API](https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html)

---

## Installation & Setup

### 1 Clone the Repository
```sh
git clone https://github.com/YOUR_GITHUB_USERNAME/exchange-rate-homework.git
cd exchange-rate-homework
```
---

### 2 Start the Backend (Spring Boot)
```sh
cd backend
mvn clean package
mvn spring-boot:run
```
---

### 3 Start the Frontend (Angular)
```sh
cd frontend
npm install
ng serve
```
---
