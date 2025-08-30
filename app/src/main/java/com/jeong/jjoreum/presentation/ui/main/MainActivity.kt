package com.jeong.jjoreum.presentation.ui.main

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.ActivityMainBinding
import com.jeong.jjoreum.presentation.ui.dialog.NetworkDialog
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import com.jeong.jjoreum.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        if (!networkManager.checkNetworkState()) {
            showNetworkDialog()
        }

        viewModel.checkUserStatus()

        val navController = (supportFragmentManager
            .findFragmentById(R.id.container_main) as NavHostFragment).navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility = when (destination.id) {
                R.id.splashFragment, R.id.joinFormFragment -> View.GONE
                else -> View.VISIBLE
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