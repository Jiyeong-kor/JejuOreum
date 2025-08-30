package com.jeong.jjoreum.presentation.ui.main

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeong.jjoreum.presentation.ui.dialog.NetworkDialog
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import com.jeong.jjoreum.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var connectivityManager: ConnectivityManager
    private val networkManager by lazy { NetworkManager(this) }
    private var networkDialog: NetworkDialog? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkDialog?.dismissAllowingStateLoss()
        }

        override fun onLost(network: Network) {
            showNetworkDialog()
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
            showNetworkDialog()
        }

        viewModel.checkUserStatus()

        setContent {
            JJOreumTheme {
                MainNavHost()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun showNetworkDialog() {
        if (networkDialog?.isAdded == true) return
        networkDialog = NetworkDialog().apply {
            setOnRetryClickListener {
                if (networkManager.checkNetworkState()) {
                    dismiss()
                }
            }
        }
        networkDialog?.show(supportFragmentManager, "network_dialog")
    }
}