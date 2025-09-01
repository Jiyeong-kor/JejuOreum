package com.jeong.jjoreum.presentation.ui.join

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.viewmodel.JoinViewModel
import com.jeong.jjoreum.util.extensions.toastMessage

@Composable
fun JoinRoute(
    onNavigateToMain: () -> Unit,
    viewModel: JoinViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isTermChecked by remember { mutableStateOf(false) }
    var showTermDialog by remember { mutableStateOf(false) }
    val auth = remember { FirebaseAuth.getInstance() }

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    context.toastMessage(context.getString(R.string.auth_failed))
                }
            }
        }
    }

    JoinFormScreen(
        viewModel = viewModel,
        isTermChecked = isTermChecked,
        onTermCheckedChange = { isTermChecked = it },
        onTermClick = { showTermDialog = true },
        onNextClick = {
            viewModel.saveNickname(
                onSuccess = {
                    context.toastMessage(context.getString(R.string.signup_success))
                    onNavigateToMain()
                },
                onFailure = {
                    context.toastMessage(context.getString(R.string.nickname_save_failed))
                }
            )
        }
    )

    if (showTermDialog) {
        Dialog(onDismissRequest = { showTermDialog = false }) {
            JoinTermDialogContent(onClose = { showTermDialog = false })
        }
    }
}
