package dev.vaidilya.cryptodha.feature.detail

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.data.model.CryptoDetailResponse
import dev.vaidilya.cryptodha.data.model.CryptoPriceList

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val priceList by viewModel.priceChartStare.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is UiState.Success<*> -> DetailContent(
                data = (uiState as UiState.Success<CryptoDetailResponse>).data,
                priceList,
                {
                    viewModel.getPriceWithInterval(it)
                }
            )
            is UiState.Error -> Text("Error: ${(uiState as UiState.Error).message}")
            UiState.Loading -> Text("Loading...")
        }
    }
}

@Composable
fun DetailContent(
    data: CryptoDetailResponse,
    price:UiState,
    onIntervalChange:(String)-> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button({}, modifier = Modifier
                    .width(0.dp)
                    .weight(8f)) { Text("Buy") }
                Spacer(Modifier
                    .width(0.dp)
                    .weight(1f))
                Button({}, modifier = Modifier
                    .width(0.dp)
                    .weight(8f)) { Text("Sell") }
            }
        }
    ){ paddingValues ->
        Column(
            modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(data.data.name, style = MaterialTheme.typography.headlineLarge)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(data.data.priceUsd, style = MaterialTheme.typography.displayMedium)
                Text("USD", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
            }
            val isNegative=(data.data.changePercent24Hr[0] == '-');
            Row(
                Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val indColor = if (isNegative) Color.Red else Color.Green
                val per = (if (isNegative) "▼ " else "▲ ")+data.data.changePercent24Hr.substring(1, 4)
                Text(
                    text = per,
                    modifier = Modifier
                        .background(
                            color = indColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = "VWAP $ ${data.data.vwap24Hr}",
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            PriceChart(price,onIntervalChange)
            StatCard(label = "Market Cap", value = data.data.marketCapUsd)
            StatCard(label = "24h Volume", value = data.data.volumeUsd24Hr)
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp, vertical = 12.dp)
//            )
//            {
//                Column(modifier = Modifier
//                    .padding(horizontal = 18.dp, vertical = 18.dp)
//                    .fillMaxWidth()) {
//                    Text("Circulating Supply", style = MaterialTheme.typography.titleMedium)
//                    LinearProgressIndicator(
//                        progress = {
//                            val supply = data.data.supply.toDoubleOrNull() ?: 0.0
//                            val maxSupply = data.data.maxSupply?.toDoubleOrNull()
//                                ?.takeIf { it > 0 } ?: 1.0
//                            (supply / maxSupply).toFloat().coerceIn(0f, 1f)
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 12.dp)
//                    )
//                }
//            }
        }
    }
}


@Composable
private fun PriceChart(
    price:UiState,
    onIntervalChange:(String)-> Unit
) {
    when (price) {
        is UiState.Success<*> -> {
            val data=(price as UiState.Success<CryptoPriceList>).data
            Log.d("ß", "PriceChart: ${data.data.size}")
            Canvas(
                Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                val width = size.width
                val height = size.height
                val dataPoints = data.data.map { it.priceUsd.toDoubleOrNull() ?: 0.0 }
                val minVal = dataPoints.min()
                val maxVal = dataPoints.max()
                val range = (maxVal - minVal).takeIf { it > 0 } ?: 1.0
                fun normalize(v: Double) = height - ((v - minVal) / range * height).toFloat()
                val stepX = width / (dataPoints.size - 1).toFloat()
                val path = Path()
                path.moveTo(0f, normalize(dataPoints[0]))
                for (i in 1 until dataPoints.size) {
                    val prevX = (i - 1) * stepX
                    val currX = i * stepX
                    path.cubicTo(
                        prevX + stepX / 2f,
                        normalize(dataPoints[i - 1]),
                        currX - stepX / 2f,
                        normalize(dataPoints[i]),
                        currX,
                        normalize(dataPoints[i])
                    )
                }
                drawPath(
                    path,
                    Color.Black,
                    style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
        else -> {
            val textMeasurer = rememberTextMeasurer()
            Canvas(
                Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                drawText(textMeasurer, "Loading....",)
            }
        }
    }
    var selectedIndex by remember { mutableStateOf(2) }
    val options = listOf("m15", "m30", "h1", "h6", "d1")
    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex = index
                    onIntervalChange(options[index])
                      },
                selected = index == selectedIndex
            ) {
                Text(label)
            }
        }
    }
//    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//        listOf().forEach { Text(it) }
//    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 12.dp)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
