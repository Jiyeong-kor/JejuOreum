package com.jeong.jjoreum.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeong.core.ui.dialog.NetworkDialog
import com.jeong.core.ui.theme.JJOreumTheme
import com.jeong.feature.join.navigation.JoinNavigation
import com.jeong.feature.oreum.navigation.OreumNavigation
import com.jeong.feature.splash.domain.model.SplashDestination
import com.jeong.feature.splash.presentation.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
            JJOreumTheme {
                val splashUiState by splashViewModel.uiState.collectAsState()
                val mainUiState by mainViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    splashViewModel.initialize()
                }

                LaunchedEffect(Unit) {
                    mainViewModel.events.collectLatest { event ->
                        when (event) {
                            MainEvent.NetworkRestored -> splashViewModel.initialize()
                        }
                    }
                }

                splashUiState.destination?.let { destination ->
                    val startDestination = when (destination) {
                        SplashDestination.Join -> JoinNavigation.ROUTE
                        SplashDestination.Map -> OreumNavigation.MAP
                    }
                    MainNavHost(startDestination)
                }
                if (mainUiState.showNetworkDialog) {
                    NetworkDialog(onRetryClick = mainViewModel::onRetryClicked)
                }
            }
        }
    }
}
