package com.jeong.jejuoreum.app.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.feature.map.navigation.ListPlaceholderDestination
import com.jeong.jejuoreum.feature.detail.navigation.DetailRouteContract
import com.jeong.jejuoreum.feature.detail.navigation.WriteReviewRouteContract
import com.jeong.jejuoreum.feature.map.navigation.MapNavigation
import com.jeong.jejuoreum.feature.map.presentation.MainViewModel
import com.jeong.jejuoreum.feature.map.presentation.main.MainSideEffect
import com.jeong.jejuoreum.feature.map.presentation.main.MainUiEvent
import com.jeong.jejuoreum.core.ui.app.JejuOreumApp
import com.jeong.jejuoreum.feature.onboarding.navigation.JoinNavigation
import com.jeong.jejuoreum.feature.onboarding.navigation.JoinRouteContract
import com.jeong.jejuoreum.feature.profile.navigation.ProfileNavigation
import com.jeong.jejuoreum.feature.splash.domain.model.SplashDestination
import com.jeong.jejuoreum.feature.splash.presentation.SplashUiEffect
import com.jeong.jejuoreum.feature.splash.presentation.SplashUiEvent
import com.jeong.jejuoreum.feature.splash.presentation.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.uiState.value.isLoading }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val splashUiState by splashViewModel.uiState.collectAsState()
            val mainUiState by mainViewModel.uiState.collectAsState()
            val context = LocalContext.current
            var startDestination by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                splashViewModel.onEvent(SplashUiEvent.Initialize)
                launch {
                    splashViewModel.effect.collectLatest { effect ->
                        when (effect) {
                            is SplashUiEffect.NavigateTo -> {
                                startDestination = when (effect.destination) {
                                    SplashDestination.Join -> JoinNavigation.ROUTE
                                    SplashDestination.Map -> OreumNavigation.MAP
                                }
                            }
                        }
                    }
                }
                launch {
                    mainViewModel.effect.collectLatest { effect ->
                        when (effect) {
                            MainSideEffect.NetworkRestored -> splashViewModel.onEvent(SplashUiEvent.Initialize)
                        }
                    }
                }
            }

            LaunchedEffect(splashUiState.errorMessage) {
                splashUiState.errorMessage?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    splashViewModel.onEvent(SplashUiEvent.ErrorConsumed)
                }
            }

            val bottomDestinations = remember {
                listOf(
                    MapNavigation,
                    ListPlaceholderDestination,
                    ProfileNavigation
                )
            }
            val destinations = remember {
                listOf(
                    JoinRouteContract,
                    MapNavigation,
                    ListPlaceholderDestination,
                    ProfileNavigation,
                    DetailRouteContract,
                    WriteReviewRouteContract
                )
            }
            JejuOreumApp(
                startDestination = startDestination,
                destinations = destinations,
                bottomDestinations = bottomDestinations,
                showNetworkDialog = mainUiState.showNetworkDialog,
                onRetryClick = { mainViewModel.onEvent(MainUiEvent.RetryClicked) }
            )
        }
    }
}
