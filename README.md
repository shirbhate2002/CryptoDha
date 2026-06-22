# CryptoDha

A paper trading app for Android where you start with **$10,000 virtual USD** and trade real cryptocurrencies at live market prices — no real money, all the market experience.

---

## Features

- **Live Market Data** — Top 20 cryptocurrencies powered by the [CoinCap API](https://docs.coincap.io/)
- **Price Charts** — Interactive price history with selectable intervals: M15, M30, H1, H6, D1
- **Buy / Sell** *(in progress)* — Trade at real-time prices against your virtual cash balance
- **Holdings Screen** *(in progress)* — Track your portfolio value, P&L, and individual positions
- **Profile Screen** *(in progress)* — View your balance and reset your portfolio back to $10,000

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + StateFlow |
| Navigation | Navigation3 (`androidx.navigation3`) |
| Networking | Retrofit 3 + Gson |
| Image Loading | Coil 3 |
| Local Storage | Room *(planned)* |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## Architecture

```
dev.vaidilya.cryptodha/
├── core/                   # Shared utilities (UiState, PriceFormatter)
├── data/
│   ├── model/              # API response data classes
│   ├── remote/             # Retrofit service interface
│   └── local/              # Room database (coming soon)
├── feature/
│   ├── home/               # HomeScreen + HomeViewModel
│   ├── detail/             # DetailScreen + DetailViewModel
│   ├── holdings/           # HoldingsScreen (WIP)
│   └── profile/            # ProfileScreen (WIP)
└── ui/
    ├── component/          # Shared composables (PriceLineChart)
    └── theme/              # Material3 theme
```

---

## Getting Started

### Prerequisites

- Android Studio Meerkat or later
- JDK 11+
- A free API key from [CoinCap](https://docs.coincap.io/)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/vaidilyashirbhate/CryptoDha.git
   cd CryptoDha
   ```

2. Add your CoinCap API key in `MainActivity.kt`:
   ```kotlin
   .addHeader("Authorization", "Bearer YOUR_API_KEY_HERE")
   ```

3. Open in Android Studio and run on a device or emulator (API 24+).

---

## License

MIT
