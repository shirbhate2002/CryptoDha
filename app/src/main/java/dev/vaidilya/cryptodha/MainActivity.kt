package dev.vaidilya.cryptodha

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.room.Room
import dev.vaidilya.cryptodha.data.local.AppDatabase
import dev.vaidilya.cryptodha.data.local.PortfolioPreferences
import dev.vaidilya.cryptodha.data.model.CryptoListItem
import dev.vaidilya.cryptodha.data.remote.CoingeckoService
import dev.vaidilya.cryptodha.data.repository.PortfolioRepository
import dev.vaidilya.cryptodha.feature.detail.DetailScreen
import dev.vaidilya.cryptodha.feature.detail.DetailViewModel
import dev.vaidilya.cryptodha.feature.holdings.HoldingsScreen
import dev.vaidilya.cryptodha.feature.holdings.HoldingsViewModel
import dev.vaidilya.cryptodha.feature.home.HomeScreen
import dev.vaidilya.cryptodha.feature.home.HomeViewModel
import dev.vaidilya.cryptodha.feature.profile.ProfileScreen
import dev.vaidilya.cryptodha.ui.theme.CryptoDhaTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Serializable
sealed class ScreenDestinations : NavKey

@Serializable
enum class BottomNavDestinations(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Outlined.Home),
    Holdings("Holdings", Icons.Outlined.Menu),
    Profile("Profile", Icons.Outlined.AccountCircle)
}

@Serializable object Home : ScreenDestinations()
@Serializable data class CryptoDetail(val cryptoData: CryptoListItem) : ScreenDestinations()
@Serializable object Holdings : ScreenDestinations()
@Serializable object Profile : ScreenDestinations()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as CryptoDhaApplication).appContainer
        val cryptoService = appContainer.cryptoService
        val portfolioRepository = appContainer.portfolioRepository

        setContent {
            CryptoDhaTheme {
                val backStack = rememberNavBackStack(Home)
                var selectedDestination by rememberSaveable { mutableIntStateOf(BottomNavDestinations.Home.ordinal) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = backStack.lastOrNull() !is CryptoDetail,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            NavigationBar {
                                BottomNavDestinations.entries.forEachIndexed { index, destination ->
                                    NavigationBarItem(
                                        selected = selectedDestination == index,
                                        onClick = {
                                            when (destination) {
                                                BottomNavDestinations.Home -> backStack.add(Home)
                                                BottomNavDestinations.Holdings -> backStack.add(Holdings)
                                                BottomNavDestinations.Profile -> backStack.add(Profile)
                                            }
                                            selectedDestination = index
                                        },
                                        icon = { Icon(destination.icon, destination.label) },
                                        label = { Text(destination.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        backStack = backStack,
                        entryProvider = entryProvider {
                            entry<Home> {
                                val vm = remember { HomeViewModel(cryptoService) }
                                HomeScreen(
                                    viewModel = vm,
                                    onSelect = { backStack.add(CryptoDetail(it)) }
                                )
                            }
                            entry<CryptoDetail> {
                                val vm = remember { DetailViewModel(it.cryptoData, cryptoService,portfolioRepository)}
                                DetailScreen(viewModel = vm)
                            }
                            entry<Holdings> {
                                val vm = remember { HoldingsViewModel(cryptoService,portfolioRepository)}
                                HoldingsScreen(vm)
                            }
                            entry<Profile> { ProfileScreen() }
                        }
                    )
                }
            }
        }
    }
}
