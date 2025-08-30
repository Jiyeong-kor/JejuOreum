package com.jeong.jjoreum.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.DialogFragment
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme

/**
 * 네트워크 연결이 없을 때 다시 시도를 요청하는 다이얼로그
 */
class NetworkDialog : DialogFragment() {

    private var onRetryClick: (() -> Unit)? = null

    fun setOnRetryClickListener(listener: () -> Unit) {
        onRetryClick = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    NetworkDialogContent(
                        onRetryClick = { onRetryClick?.invoke() }
                    )
                }
            }
        }
    }

    @Composable
    private fun NetworkDialogContent(onRetryClick: () -> Unit) {
        Card {
            Column {
                Text(
                    text = stringResource(R.string.network_error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(R.dimen.network_error_text_padding_horizontal),
                            vertical = dimensionResource(R.dimen.network_error_text_padding_vertical),
                        ),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = dimensionResource(R.dimen.network_border),
                    color = DividerDefaults.color
                )
                TextButton(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.network_retry))
                }
            }
        }
    }
}