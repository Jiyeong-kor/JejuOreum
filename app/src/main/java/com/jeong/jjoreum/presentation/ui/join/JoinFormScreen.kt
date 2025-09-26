package com.jeong.jjoreum.presentation.ui.join

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jeong.domain.model.NicknameValidationResult
import com.jeong.jjoreum.R

@Composable
fun JoinFormScreen(
    uiState: JoinUiState,
    isTermChecked: Boolean,
    onNicknameChange: (String) -> Unit,
    onTermCheckedChange: (Boolean) -> Unit,
    onTermClick: () -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isChecking = uiState.availability is NicknameAvailabilityState.Checking
    val isInvalid = uiState.validation is NicknameValidationResult.Invalid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.join_form_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = uiState.nickname,
            onValueChange = onNicknameChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                if (uiState.canSubmit && isTermChecked) {
                    onSubmit()
                }
            }),
            trailingIcon = {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            isError = uiState.hasUserInteracted && isInvalid
        )

        Text(
            text = stringResource(id = R.string.nickname_rule),
            style = MaterialTheme.typography.bodySmall,
            color = if (uiState.hasUserInteracted && isInvalid) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(top = 8.dp)
        )
        when (uiState.availability) {
            NicknameAvailabilityState.Available -> {
                Text(
                    text = stringResource(id = R.string.nickname_available),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            NicknameAvailabilityState.Unavailable -> {
                Text(
                    text = stringResource(id = R.string.nickname_already_exists),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            is NicknameAvailabilityState.Error -> {
                Text(
                    text = stringResource(id = R.string.nickname_check_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            NicknameAvailabilityState.Idle,
            NicknameAvailabilityState.Checking -> Unit
        }

        Text(
            text = stringResource(id = R.string.term_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isTermChecked,
                onCheckedChange = onTermCheckedChange
            )
            Text(
                text = stringResource(id = R.string.term_text),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = onTermClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.term_arrow_desc)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSubmit,
            enabled = isTermChecked && uiState.canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(id = R.string.join_form_btn_next),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
