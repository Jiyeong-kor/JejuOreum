package com.jeong.jjoreum.presentation.ui.main

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeong.jjoreum.presentation.ui.dialog.NetworkDialog
import com.jeong.jjoreum.presentation.ui.splash.SplashUiState
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import com.jeong.jjoreum.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var connectivityManager: ConnectivityManager
    private val networkManager by lazy { NetworkManager(this) }
    private var showNetworkDialog by mutableStateOf(false)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            showNetworkDialog = false
        }

        override fun onLost(network: Network) {
            showNetworkDialog = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        if (!networkManager.checkNetworkState()) {
            showNetworkDialog = true
        }

        viewModel.checkUserStatus()

        setContent {
            JJOreumTheme {
                val uiState by viewModel.uiState.observeAsState()
                uiState?.let { state ->
                    val startDestination = when (state) {
                        SplashUiState.GoToJoin -> "join"
                        SplashUiState.GoToMap -> "map"
                    }
                    MainNavHost(startDestination)
                    if (showNetworkDialog) {
                        NetworkDialog(
                            onRetryClick = {
                                if (networkManager.checkNetworkState()) {
                                    showNetworkDialog = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}