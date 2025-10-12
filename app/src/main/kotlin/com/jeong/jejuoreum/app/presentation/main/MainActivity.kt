package com.jeong.jejuoreum.app.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jeong.jejuoreum.core.navigation.ComposableDestination
import com.jeong.jejuoreum.core.navigation.StartDestination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var destinations: Set<@JvmSuppressWildcards ComposableDestination>

    @Inject
    @StartDestination
    lateinit var startDestination: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JejuOreumApp(
                startDestination = startDestination,
                destinations = destinations
            )
        }
    }
}
