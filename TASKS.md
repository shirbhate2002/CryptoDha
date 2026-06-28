# CryptoDha — Paper Trading App: Full Task Breakdown

## What This App Does
Users start with $10,000 virtual USD and can buy/sell real cryptocurrencies at live CoinCap prices. All trades are simulated. Holdings screen tracks their portfolio. Profile screen has settings (reset portfolio, etc.).

---

## ✅ Already Done

| # | Task | Notes |
|---|------|-------|
| 1 | MVVM package structure | `core/`, `data/model/`, `data/remote/`, `feature/*`, `ui/component/` |
| 2 | HomeScreen — live crypto list | Fetches top 20, shows icon/name/symbol/price. Fully functional. |
| 3 | Navigation | NavDisplay + bottom nav (Home, Holdings, Profile). Bottom bar hides on detail screen. |
| 4 | DetailScreen — basic real data | Shows name, live price, 24h change% with red/green color. |
| 5 | API: list + detail endpoints | `GET /assets?limit=20`, `GET /assets/{slug}` both working. |
| 6 | `formatCryptoPrice()` utility | Smart decimal formatting. `core/PriceFormatter.kt` |
| 7 | `UiState` sealed class | Loading / Success\<T\> / Error. `core/UiState.kt` |
| 8 | `PriceLineChart` component | Vico chart composable exists — **but unused** in DetailScreen. `ui/component/PriceLineChart.kt` |
| 9 | `PriceHistory` data model | `data/model/PriceHistory.kt` |

---

## 🔲 Tasks To Complete

### Phase 1 — Foundation (Database + DI)
> Must be done before Phase 3, 4, 5.

| # | Task | Files | Notes | Status |
|---|------|-------|-------|--------|
| F1 | Add Room + KSP + Coroutines to gradle | `app/build.gradle.kts` | Add `room-runtime`, `room-ktx`, `room-compiler` (KSP), `kotlinx-coroutines-android` | ✅     |
| F2 | Define `HoldingEntity` + `TradeEntity` | `data/local/entity/HoldingEntity.kt`, `TradeEntity.kt` | Holding: cryptoId, name, symbol, amountHeld, avgBuyPrice. Trade: cryptoId, type(BUY/SELL), amount, priceAtTrade, timestamp |✅|
| F3 | Create DAOs | `data/local/dao/HoldingDao.kt`, `TradeDao.kt` | CRUD for holdings, insert trades, query by cryptoId |✅|
| F4 | Create `AppDatabase` | `data/local/AppDatabase.kt` | Room singleton with both entities + DAOs |✅|
| F5 | `PortfolioPreferences` | `data/local/PortfolioPreferences.kt` | SharedPreferences wrapper — stores cash balance (default $10,000) |✅|
| F6 | `PortfolioRepository` | `data/repository/PortfolioRepository.kt` | `buyAsset()`, `sellAsset()`, `resetPortfolio()` — all atomic via Room `withTransaction` |⏳|
| F7 | `AppContainer` + `CryptoDhaApplication` | `CryptoDhaApplication.kt`, `AppContainer.kt` | Manual DI: holds Retrofit, CryptoService, AppDatabase, Repository singletons |✅|
| F8 | Wire `AppContainer` into `MainActivity` | `MainActivity.kt` | Replace inline `retrofit.create()` calls with `appContainer` references |✅|

---

### Phase 2 — Detail Screen: Real Data & Real Chart
> Can start immediately, no Phase 1 dependency.

| # | Task | Files | Notes | State |
|---|------|-------|-------|----|
| D1 | Fix hardcoded stats | `feature/detail/DetailScreen.kt` | Replace `"$1.2T"` / `"$27.01B"` / `50f` with real fields: `marketCapUsd`, `volumeUsd24Hr`, `supply`, `maxSupply` | ✅   |
| D2 | Add price history API endpoint | `data/remote/CryptoService.kt`, `data/model/PriceHistoryResponse.kt` | `GET /assets/{id}/history?interval={interval}` — returns list of `{time, priceUsd}` | ✅   |
| D3 | Wire real chart in DetailScreen | `feature/detail/DetailScreen.kt`, `feature/detail/DetailViewModel.kt` | Replace fake Canvas chart with `PriceLineChart`. DetailViewModel fetches history on load (default M15) | ✅   |
| D4 | Make time period buttons functional | `feature/detail/DetailScreen.kt`, `feature/detail/DetailViewModel.kt` | M15/M30/H1/H6/D1 → re-fetch history with matching interval. Highlight selected. | ✅   |

---

### Phase 3 — Buy / Sell Trading
> Requires F6 (PortfolioRepository).

| # | Task | Files | Notes | Status|
|---|------|-------|-------|-------|
| T1 | Add trade logic to `DetailViewModel` | `feature/detail/DetailViewModel.kt` | `buyAsset(amountUsd)` and `sellAsset(amountCrypto)` methods. Expose `cashBalance` and `currentHolding` as state. |
| T2 | Create `TradeBottomSheet` | `feature/detail/TradeBottomSheet.kt` | Modal bottom sheet: shows current price, input field (USD for buy / crypto amount for sell), balance info, Confirm button |⏳|
| T3 | Wire Buy/Sell buttons | `feature/detail/DetailScreen.kt` | Buy button opens BUY sheet, Sell button opens SELL sheet. Show snackbar on success/failure. |

---

### Phase 4 — Holdings Screen
> Requires F6 (PortfolioRepository).

| # | Task | Files | Notes |
|---|------|-------|-------|
| H1 | Create `HoldingsViewModel` | `feature/holdings/HoldingsViewModel.kt` | Load all holdings from DB + cash balance. Fetch live price per holding. Compute total portfolio value and P&L vs $10,000. |
| H2 | Build `HoldingsScreen` UI | `feature/holdings/HoldingsScreen.kt` | Summary card: total value, cash remaining, P&L (green/red). List: icon, name, amount held, current value, profit/loss per holding. |

---

### Phase 5 — Profile Screen
> Requires F6 (PortfolioRepository).

| # | Task | Files | Notes |
|---|------|-------|-------|
| P1 | Create `ProfileViewModel` | `feature/profile/ProfileViewModel.kt` | Expose cash balance, starting balance. `resetPortfolio()` method. |
| P2 | Build `ProfileScreen` UI | `feature/profile/ProfileScreen.kt` | Show current cash balance, starting balance ($10,000), "Reset Portfolio" button with confirmation dialog. |

---

### Phase 6 — Polish & Bug Fixes
> Can be done anytime.

| # | Task | Files | Notes |
|---|------|-------|-------|
| X1 | Fix icon URL trailing newline | `feature/home/HomeScreen.kt` | Remove `\n` at end of Coil image URL string |
| X2 | Safe percentage parsing | `feature/detail/DetailScreen.kt` | `changePercent24Hr.substring(1, 4)` crashes if string < 4 chars — add length check |
| X3 | Remove empty `Tokens`/`TokensX` classes | `data/model/Tokens.kt`, `TokensX.kt` | Populate from real API shape or delete if unused |

---

## Dependency Order

```
F1 → F2 → F3 → F4 ─┐
                     ├→ F6 (PortfolioRepository) → F7 → F8
F5 ─────────────────┘         │
                              ├→ T1 → T2 → T3   (Buy/Sell)
                              ├→ H1 → H2         (Holdings)
                              └→ P1 → P2         (Profile)

D1 → D2 → D3 → D4   (parallel with F-series)
X1, X2, X3           (anytime)
```

---

## New Dependencies Needed (`app/build.gradle.kts`)

```kotlin
// Room
implementation("androidx.room:room-runtime:2.7.1")
implementation("androidx.room:room-ktx:2.7.1")
ksp("androidx.room:room-compiler:2.7.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
```

Also add KSP plugin to both `build.gradle.kts` (project level) and `app/build.gradle.kts`.


---

## Bugs find out while developing
- [ ] 2 request are going everytime we are opening the detail screen
- [ ] Gitter when we navigate between screens 


---

## Good to have features
- [ ] Zoom and pan of Graph
- [ ] add pagenation to the Home screen.
- [ ] add withTransaction in the room db 
- [ ] add shot and filter in holding section

---

## Verification Checklist
- [ ] Buy $500 of Bitcoin → cash decreases by $500, holding appears in Holdings screen
- [ ] Sell half the Bitcoin → cash increases, holding amount halves
- [ ] Holdings screen shows correct total portfolio value and P&L
- [ ] Chart switches intervals (M15 → D1) and re-renders with real data
- [ ] Reset Portfolio → balance back to $10,000, all holdings cleared
- [ ] Tap Ethereum → no crash (maxSupply null ✅ already fixed)
- [ ] App survives process death (Room + SharedPreferences persist data)