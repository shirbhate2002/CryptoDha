package dev.vaidilya.cryptodha.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries


//@Composable
//fun PriceLineChart(
//    priceHistory: List<PriceHistory>,
//    modifier: Modifier = Modifier
//) {
//    val modelProducer = remember { CartesianChartModelProducer() }
//    val minPrice = priceHistory.minOfOrNull { it.priceUsd } ?: 0f
//    val maxPrice = priceHistory.maxOfOrNull { it.priceUsd } ?: 1f
//    val padding = (maxPrice - minPrice) * 0.01f
//
//    LaunchedEffect(priceHistory) {
//        if (priceHistory.isNotEmpty()) {
//            modelProducer.runTransaction {
//                lineSeries { series(priceHistory.map { it.priceUsd }) }
//            }
//        }
//    }
//
//    CartesianChartHost(
//        chart = rememberCartesianChart(
//            rememberLineCartesianLayer(
//                rangeProvider = CartesianLayerRangeProvider.fixed(
//                    minY = (minPrice - padding).toDouble(),
//                    maxY = (maxPrice + padding).toDouble()
//                )
//            ),
//            startAxis = VerticalAxis.rememberStart(),
//            bottomAxis = HorizontalAxis.rememberBottom(),
//        ),
//        modelProducer = modelProducer,
//        modifier = modifier.fillMaxWidth().height(250.dp)
//    )
//}
//
//@Preview
//@Composable
//fun PreviewPriceLineChart() {
//    PriceLineChart(
//        priceHistory = listOf(
//            PriceHistory(1700000000000L, 36000.0f),
//            PriceHistory(1700003600000L, 36450.5f),
//            PriceHistory(1700007200000L, 35980.0f),
//        )
//    )
//}
