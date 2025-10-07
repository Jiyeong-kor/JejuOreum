package com.jeong.jjoreum.presentation.ui.main

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
import com.jeong.core.ui.dialog.NetworkDialog
import com.jeong.core.ui.theme.JJOreumTheme
import com.jeong.feature.join.navigation.JoinNavigation
import com.jeong.feature.main.presentation.MainRoute
import com.jeong.feature.main.presentation.MainSideEffect
import com.jeong.feature.main.presentation.MainUiEvent
import com.jeong.feature.oreum.navigation.OreumNavigation
import com.jeong.feature.splash.domain.model.SplashDestination
import com.jeong.feature.splash.presentation.SplashSideEffect
import com.jeong.feature.splash.presentation.SplashUiEvent
import com.jeong.feature.splash.presentation.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.state.value.isLoading }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JJOreumTheme {
                val splashUiState by splashViewModel.state.collectAsState()
                val mainUiState by mainViewModel.state.collectAsState()
                val context = LocalContext.current
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    splashViewModel.onEvent(SplashUiEvent.Initialize)
                    launch {
                        splashViewModel.effect.collectLatest { effect ->
                            when (effect) {
                                is SplashSideEffect.NavigateTo -> {
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

                startDestination?.let { destination ->
                    MainRoute(destination)
                }
                if (mainUiState.showNetworkDialog) {
                    NetworkDialog(onRetryClick = {
                        mainViewModel.onEvent(MainUiEvent.RetryClicked)
                    })
                }
            }
        }
    }
}
