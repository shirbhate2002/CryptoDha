package dev.vaidilya.cryptodha.feature.holdings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vaidilya.cryptodha.core.formatCryptoPrice
import dev.vaidilya.cryptodha.data.local.entity.HoldingEntity
import dev.vaidilya.cryptodha.data.model.CryptoListItem

@Composable
fun HoldingsScreen(
    holdingsViewModel: HoldingsViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize(),
    ) {
        val holdings=holdingsViewModel.holdings.collectAsState()

        LazyColumn(){
            items(holdings.value){
                Row(Modifier.fillMaxWidth()){
                    CryptoCard(it)
                }
            }
        }
    }
}


@Composable
fun CryptoCard(crypto: HoldingEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(crypto.coinName)
                    Text("Qty ${crypto.quantity.toString().substring(0,10)}", fontStyle = FontStyle.Italic)
                }
            }
            Column(horizontalAlignment = AbsoluteAlignment.Right) {
                val isNegative=true;
                val price_change =10;
                Text("$price_change %", color = (if(isNegative) Color.Red else Color(0xFF008000)))
                Text(formatCryptoPrice(crypto.invested.toString()))
            }
        }
        HorizontalDivider(thickness = 1.dp)
    }
}