package com.jeong.jejuoreum.app.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jeong.jejuoreum.core.navigation.NavigationProvider
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.core.ui.app.JejuOreumApp
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val navigationProvider = EntryPointAccessors.fromApplication(
            applicationContext,
            NavigationProvider::class.java
        )

        setContent {
            JejuOreumApp(
                startDestination = OreumNavigation.Splash.ROUTE,
                navigationProvider = navigationProvider,
                showNetworkDialog = false,
                onRetryClick = {}
            )
        }
    }
}
