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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vaidilya.cryptodha.core.UiState
import dev.vaidilya.cryptodha.core.formateToCompactNumber
import dev.vaidilya.cryptodha.data.model.CryptoChartData
import dev.vaidilya.cryptodha.data.model.CryptoListItem
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val priceList by viewModel.priceChartStare.collectAsState()
    val cashBalance by viewModel.cashBalance.collectAsState()
    val holdingQuantity by viewModel.holdingQuantity.collectAsState()
    val tradeState by viewModel.tradeState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is UiState.Success<*> -> DetailContent(
                data = (uiState as UiState.Success<CryptoListItem>).data,
                priceList = priceList,
                cashBalance = cashBalance,
                holdingQuantity = holdingQuantity,
                tradeState = tradeState,
                onIntervalChange = { viewModel.getPriceWithInterval(it) },
                onBuy = { amount -> viewModel.buyAsset(amount) },
                onSell = { coins -> viewModel.sellAsset(coins) },
                onSheetDismiss = { viewModel.clearTradeState() }
            )
            is UiState.Error -> Text("Error: ${(uiState as UiState.Error).message}")
            UiState.Loading -> Text("Loading...")
        }
    }
}

enum class TradeMode { Buy, Sell }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    data: CryptoListItem,
    priceList: UiState,
    cashBalance: Double,
    holdingQuantity: Double,
    tradeState: TradeResult?,
    onIntervalChange: (String) -> Unit,
    onBuy: (Double) -> Unit,
    onSell: (Double) -> Unit,
    onSheetDismiss: () -> Unit = {}
){

    var showBottomSheet by remember { mutableStateOf(false) }
    var tradeMode by remember { mutableStateOf(TradeMode.Buy) }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { tradeMode = TradeMode.Buy; showBottomSheet = true },
                    modifier = Modifier.width(0.dp).weight(8f)
                ) { Text("Buy") }
                Spacer(Modifier.width(0.dp).weight(1f))
                Button(
                    onClick = { tradeMode = TradeMode.Sell; showBottomSheet = true },
                    modifier = Modifier.width(0.dp).weight(8f)
                ) { Text("Sell") }
            }
        }
    ){ paddingValues ->
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
        ) {
            Text(data.name, style = MaterialTheme.typography.headlineLarge)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(data.currentPrice.toString(), style = MaterialTheme.typography.displayMedium)
                Text("USD", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
            }
            val isNegative=(data.priceChangePercentage24h.toString()[0] == '-');
            Row(
                Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val indColor = if (isNegative) Color.Red else Color.Green
                val per = (if (isNegative) "▼ " else "▲ ")+data.priceChangePercentage24h.toString()
                Text(
                    text = per,
                    modifier = Modifier
                        .background(
                            color = indColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            PriceChart(priceList,onIntervalChange)
            StatCard(label = "Market Cap", value = formateToCompactNumber("%.0f".format(data.marketCap)))
            StatCard(label = "24h Volume", value = formateToCompactNumber("%.0f".format(data.marketCapChange24h)))
        }
        if (showBottomSheet) {
            TradeBottomSheet(
                mode = tradeMode,
                onDismiss = {
                    showBottomSheet = false
                    onSheetDismiss()
                },
                coinSymbol = data.image,
                coinName = data.name,
                coinCurrentPrice = data.currentPrice,
                cashBalance = cashBalance,
                holdingQuantity = holdingQuantity,
                tradeState = tradeState,
                onBuy = onBuy,
                onSell = onSell
            )
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
            val data=(price as UiState.Success<CryptoChartData>).data
            Log.d("Price Data recevied!!", "PriceChart: ${data.prices.size}")
            Canvas(
                Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                val width = size.width
                val height = size.height
                val dataPoints = data.prices.map{it[1]}
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
    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("1D","2D","7D","15D","30D","60D")
    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex = index
                    onIntervalChange(options[index].removeSuffix("D"))
                          },
                selected = index == selectedIndex
            ) {
                Text(label)
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
private fun PreviewBuySheet() {
    TradeBottomSheetContent(
        mode = TradeMode.Buy,
        coinSymbol = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
        coinName = "Bitcoin",
        coinCurrentPrice = 60123.0,
        cashBalance = 10000.0,
        holdingQuantity = 0.0,
        onDismiss = {},
        onTrade = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSellSheet() {
    TradeBottomSheetContent(
        mode = TradeMode.Sell,
        coinSymbol = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
        coinName = "Bitcoin",
        coinCurrentPrice = 60123.0,
        cashBalance = 7500.0,
        holdingQuantity = 0.042,
        onDismiss = {},
        onTrade = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TradeBottomSheet(
    mode: TradeMode,
    onDismiss: () -> Unit,
    coinSymbol: String,
    coinName: String,
    coinCurrentPrice: Double,
    cashBalance: Double,
    holdingQuantity: Double,
    tradeState: TradeResult?,
    onBuy: (Double) -> Unit,
    onSell: (Double) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        TradeBottomSheetContent(
            mode = mode,
            coinSymbol = coinSymbol,
            coinName = coinName,
            coinCurrentPrice = coinCurrentPrice,
            cashBalance = cashBalance,
            holdingQuantity = holdingQuantity,
            tradeState = tradeState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismiss()
                }
            },
            onTrade = { amount -> if (mode == TradeMode.Buy) onBuy(amount) else onSell(amount) }
        )
    }
}

@Composable
private fun TradeBottomSheetContent(
    mode: TradeMode,
    coinSymbol: String,
    coinName: String,
    coinCurrentPrice: Double,
    cashBalance: Double,
    holdingQuantity: Double,
    tradeState: TradeResult? = null,
    onDismiss: () -> Unit,
    onTrade: (Double) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    val isBuy = mode == TradeMode.Buy
    val actionLabel = if (isBuy) "BUY" else "SELL"
    val availableLabel = if (isBuy) "$ $cashBalance" else "$holdingQuantity coins"
    val inputPrefix = if (isBuy) "$" else ""

    Column(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                modifier = Modifier.size(32.dp),
                model = coinSymbol,
                contentDescription = "$coinName Coin Icon",
            )
            Column(Modifier.padding(horizontal = 8.dp)) {
                Text("${if (isBuy) "Buy" else "Sell"} $coinName")
                Text("Current Price: $coinCurrentPrice")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            }
        }
        Column(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                prefix = { if (inputPrefix.isNotEmpty()) Text(inputPrefix) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Outlined.MailOutline,
                contentDescription = ""
            )
            Text("Available", modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(Modifier.weight(1f))
            Text(availableLabel, color = Color.Blue)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(42.dp),
            onClick = { onTrade(textInput.toDoubleOrNull() ?: 0.0) }
        ) {
            Text(actionLabel)
        }
        tradeState?.let {
            val (color, message) = when (it) {
                is TradeResult.Success -> Color(0xFF388E3C) to it.message
                is TradeResult.Failure -> Color(0xFFD32F2F) to it.message
            }
            Text(
                text = message,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}