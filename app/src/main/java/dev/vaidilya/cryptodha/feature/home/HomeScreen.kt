package dev.vaidilya.cryptodha.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.data.model.Crypto

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSelect: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is UiState.Success<*> -> CryptoList(
            cryptos = (uiState as UiState.Success<List<Crypto>>).data,
            onSelect = onSelect
        )
        is UiState.Error -> Text("Error: ${(uiState as UiState.Error).message}")
        UiState.Loading -> Unit
    }
}

@Composable
fun CryptoList(cryptos: List<Crypto>, onSelect: (String) -> Unit) {
    LazyColumn {
        items(cryptos) { crypto ->
            CryptoCard(crypto, onSelect)
        }
    }
}

@Composable
fun CryptoCard(crypto: Crypto, onSelect: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        onClick = { onSelect(crypto.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier.size(32.dp),
                    model = "https://assets.coincap.io/assets/icons/${crypto.symbol.lowercase()}@2x.png\n",
                    contentDescription = "${crypto.name} Coin Icon",
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(crypto.name)
                    Text(crypto.symbol, fontStyle = FontStyle.Italic)
                }
            }
            Text(crypto.priceUsd)
        }
        HorizontalDivider(thickness = 1.dp)
    }
}
