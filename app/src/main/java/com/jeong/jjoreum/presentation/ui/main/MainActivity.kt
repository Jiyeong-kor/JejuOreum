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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.ActivityMainBinding
import com.jeong.jjoreum.presentation.ui.dialog.NetworkDialog
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
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

        binding.bottomNav.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JJOreumTheme {
                    MainBottomNavigation(navController)
                }
            }
        }

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

    @Composable
    private fun MainBottomNavigation(navController: NavController) {
        val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(
            initialValue = navController.currentBackStackEntry
        )
        val currentDestination = navBackStackEntry?.destination

        NavigationBar {
            NavigationBarItem(
                selected = currentDestination?.id == R.id.mapFragment,
                onClick = {
                    navController.navigate(
                        resId = R.id.mapFragment,
                        args = null,
                        navOptions = navOptions {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    )
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_map),
                        contentDescription = stringResource(R.string.map_title)
                    )
                },
                label = { Text(stringResource(R.string.map_title)) }
            )
            NavigationBarItem(
                selected = currentDestination?.id == R.id.listFragment,
                onClick = {
                    navController.navigate(
                        resId = R.id.listFragment,
                        args = null,
                        navOptions = navOptions {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    )
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_list),
                        contentDescription = stringResource(R.string.list_title)
                    )
                },
                label = { Text(stringResource(R.string.list_title)) }
            )
            val isMySelected = currentDestination?.id == R.id.navigation_my
            NavigationBarItem(
                selected = isMySelected,
                onClick = {
                    navController.navigate(
                        resId = R.id.navigation_my,
                        args = null,
                        navOptions = navOptions {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    )
                },
                icon = {
                    val iconRes = if (isMySelected) R.drawable.ic_my_selected else R.drawable.ic_my_unselected
                    Icon(
                        painterResource(iconRes),
                        contentDescription = stringResource(R.string.my_title)
                    )
                },
                label = { Text(stringResource(R.string.my_title)) }
            )
        }
    }
}