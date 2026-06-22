package dev.vaidilya.cryptodha.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.core.formatCryptoPrice
import dev.vaidilya.cryptodha.data.remote.CryptoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val cryptoService: CryptoService
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val response = cryptoService.getCurrentMarketList()
                _uiState.update {
                    val formatted = response.data.map { item ->
                        item.copy(priceUsd = formatCryptoPrice(item.priceUsd))
                    }
                    UiState.Success(formatted)
                }
                Log.d("HomeViewModel", "Loaded ${response.data.size} assets")
            } catch (e: Exception) {
                _uiState.update { UiState.Error("Something went wrong!!") }
                Log.d("HomeViewModel", e.toString())
            }
        }
    }
}
