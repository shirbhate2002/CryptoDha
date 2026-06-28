package dev.vaidilya.cryptodha.feature.holdings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.data.local.entity.HoldingEntity
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import dev.vaidilya.cryptodha.data.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class HoldingsViewModel(
    private final val cryptoService: CoingeckoService,
    private final val portfolioRepository: PortfolioRepository
): ViewModel() {

    val holdings: StateFlow<List<HoldingEntity>> = portfolioRepository
        .getAllHoldings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashBalance: StateFlow<Double> = flow {
        emit(portfolioRepository.getCashBalance())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10_000.0)
}