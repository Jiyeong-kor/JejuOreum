package com.jeong.jejuoreum.feature.splash.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.jejuoreum.core.ui.extensions.asString
import com.jeong.jejuoreum.feature.splash.domain.model.SplashDestination
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashRoute(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SplashUiEvent.Initialize)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashUiEffect.NavigateTo -> when (effect.destination) {
                    SplashDestination.Join -> onNavigateToOnboarding()
                    SplashDestination.Map -> onNavigateToMap()
                }
            }
        }
    }

    uiState.value.errorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message.asString(context), Toast.LENGTH_SHORT).show()
            viewModel.onEvent(SplashUiEvent.ErrorConsumed)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.value.isLoading) {
            CircularProgressIndicator()
        }
    }
}
