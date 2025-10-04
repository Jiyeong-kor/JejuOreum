package com.jeong.feature.join.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.core.ui.extensions.toastMessage
import com.jeong.feature.join.R

@Composable
fun JoinRoute(
    onNavigateToMain: () -> Unit,
    viewModel: JoinViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isTermChecked by remember { mutableStateOf(false) }
    var showTermDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                JoinEvent.AuthenticationFailed -> {
                    context.toastMessage(R.string.auth_failed)
                }

                is JoinEvent.NicknameSaved -> {
                    context.toastMessage(R.string.signup_success)
                    onNavigateToMain()
                }

                JoinEvent.NicknameSaveFailed -> {
                    context.toastMessage(R.string.nickname_save_failed)
                }
            }
        }
    }

    JoinFormScreen(
        uiState = uiState,
        isTermChecked = isTermChecked,
        onNicknameChange = viewModel::onNicknameChanged,
        onTermCheckedChange = { isChecked -> isTermChecked = isChecked },
        onTermClick = { showTermDialog = true },
        onSubmit = {
            if (isTermChecked) {
                viewModel.submitNickname()
            }
        }
    )

    if (showTermDialog) {
        Dialog(onDismissRequest = { showTermDialog = false }) {
            JoinTermDialogContent(onClose = { showTermDialog = false })
        }
    }
}
