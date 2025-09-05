package com.jeong.jjoreum.feature.join.presentation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jeong.jjoreum.R
import com.jeong.jjoreum.feature.join.presentation.JoinViewModel

@Composable
fun JoinFormScreen(
    viewModel: JoinViewModel,
    isTermChecked: Boolean,
    onTermCheckedChange: (Boolean) -> Unit,
    onTermClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val nickname by viewModel.nickname.collectAsState()
    val isLoading by viewModel.isLoadingNicknameAvailability.collectAsState()
    val isInvalid by viewModel.isNicknameInvalid.collectAsState()
    val errorMessage by viewModel.nicknameErrorMessage.collectAsState()
    val isAvailable by viewModel.isNicknameAvailable.collectAsState()
    val hasTyped by viewModel.hasUserTyped.collectAsState()

    val focusManager = LocalFocusManager.current

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
            value = nickname,
            onValueChange = { viewModel.updateNickname(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.updateNickname(nickname.trim())
                focusManager.clearFocus()
            }),
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            isError = hasTyped && isInvalid
        )

        Text(
            text = stringResource(id = R.string.nickname_rule),
            style = MaterialTheme.typography.bodySmall,
            color = if (hasTyped && isInvalid)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (errorMessage != null || isAvailable) {
            val color = if (isAvailable)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
            Text(
                text = errorMessage ?: stringResource(id = R.string.nickname_available),
                style = MaterialTheme.typography.bodySmall,
                color = color,
                modifier = Modifier.padding(top = 4.dp)
            )
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
            onClick = onNextClick,
            enabled = isTermChecked && !isLoading && !isInvalid && isAvailable,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.join_form_btn_next),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}