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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.core.formatCryptoPrice
import dev.vaidilya.cryptodha.data.model.CryptoList
import dev.vaidilya.cryptodha.data.model.CryptoListItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSelect: (CryptoListItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is UiState.Success<*> -> CryptoList(
            cryptos = (uiState as UiState.Success<CryptoList>).data,
            onSelect = onSelect
        )
        is UiState.Error -> Text("Error: ${(uiState as UiState.Error).message}")
        UiState.Loading -> Unit
    }
}

@Composable
fun CryptoList(cryptos: CryptoList, onSelect: (CryptoListItem) -> Unit) {
    LazyColumn {
        items(cryptos) { crypto ->
            CryptoCard(crypto, onSelect)
        }
    }
}

@Composable
fun CryptoCard(crypto: CryptoListItem, onSelect: (CryptoListItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        onClick = { onSelect(crypto) }
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
                    model = crypto.image,
                    contentDescription = "${crypto.name} Coin Icon",
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(crypto.name)
                    Text(crypto.symbol, fontStyle = FontStyle.Italic)
                }
            }
            Column(horizontalAlignment = AbsoluteAlignment.Right) {
                Text(formatCryptoPrice(crypto.current_price.toString()))
                val price_change=crypto.price_change_percentage_24h.toString();
                val isNegative=(price_change[0] == '-');
                Text("$price_change %", color = (if(isNegative) Color.Red else Color(0xFF008000)))
            }
        }
        HorizontalDivider(thickness = 1.dp)
    }
}
