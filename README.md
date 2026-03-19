# Trading Platform

A demo crypto trading platform built with Spring Boot and H2 in-memory database.

## Features
- Aggregate best prices every 10 seconds from Binance and Huobi
- Get latest aggregated price
- Execute BUY/SELL trade
- Wallet balance
- Trading history

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- Maven

## Supported Pairs
- BTCUSDT
- ETHUSDT

## Assumptions
- User(userId = 1) is already authenticated
- Demo user is seeded automatically
- Initial wallet balance: 50,000 USDT

## Run
```bash
mvn spring-boot:run
```

## Api Endpoints and examples
### 1. Latest price
GET http://localhost:8080/api/prices/latest?pair=BTCUSDT

```bash
curl --location 'http://localhost:8080/api/prices/latest?pair=BTCUSDT'
```

### 2. Trade
POST http://localhost:8080/api/trades

- Buy BTC
```bash
curl --location 'http://localhost:8080/api/trades' \
--header 'Idempotency-Key: buy-btc-001' \
--header 'Content-Type: application/json' \
--data '{
    "pair": "BTCUSDT",
    "side": "BUY",
    "quantity": 0.1
}'
```

- Sell BTC
```bash
curl --location 'http://localhost:8080/api/trades' \
--header 'Idempotency-Key: sell-btc-001' \
--header 'Content-Type: application/json' \
--data '{
    "pair": "BTCUSDT",
    "side": "SELL",
    "quantity": 0.05
}'
```

- Buy ETH
```bash
curl --location 'http://localhost:8080/api/trades' \
--header 'Idempotency-Key: buy-eth-001' \
--header 'Content-Type: application/json' \
--data '{
    "pair": "ETHUSDT",
    "side": "BUY",
    "quantity": 0.5
}'
```

- Sell ETH
```bash
curl --location 'http://localhost:8080/api/trades' \
--header 'Idempotency-Key: sell-eth-001' \
--header 'Content-Type: application/json' \
--data '{
    "pair": "ETHUSDT",
    "side": "SELL",
    "quantity": 0.3
}'
```

### 3. Wallet balance
GET http://localhost:8080/api/wallets/balance

```bash
curl --location 'http://localhost:8080/api/wallets/balance'
```

### 4. Trade history
GET http://localhost:8080/api/trades/history

```bash
curl --location 'http://localhost:8080/api/trades/history'
```
