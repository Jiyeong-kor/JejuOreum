package com.jeong.jjoreum.presentation.ui.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeong.core.ui.dialog.NetworkDialog
import com.jeong.feature.splash.domain.model.SplashDestination
import com.jeong.core.ui.theme.JJOreumTheme
import com.jeong.feature.splash.presentation.SplashViewModel
import com.jeong.jjoreum.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint
import com.jeong.feature.join.navigation.JoinNavigation
import com.jeong.feature.oreum.navigation.OreumNavigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private val networkManager by lazy { NetworkManager(this) }
    private var showNetworkDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.isLoading }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        networkManager.registerNetworkCallback(
            onAvailable = { showNetworkDialog = false },
            onLost = { showNetworkDialog = true })
        if (!networkManager.checkNetworkState()) {
            showNetworkDialog = true
        }
        viewModel.initialize()
        setContent {
            JJOreumTheme {
                val uiState by viewModel.uiState.collectAsState()
                uiState.destination?.let { destination ->
                    val startDestination = when (destination) {
                        SplashDestination.Join -> JoinNavigation.ROUTE
                        SplashDestination.Map -> OreumNavigation.MAP
                    }
                    MainNavHost(startDestination)
                }
                if (showNetworkDialog) {
                    NetworkDialog(
                        onRetryClick = {
                            if (networkManager.checkNetworkState()) {
                                showNetworkDialog = false
                                viewModel.initialize()
                            }
                        })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkManager.unregisterNetworkCallback()
    }
}
