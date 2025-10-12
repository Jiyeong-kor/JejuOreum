package com.jeong.jejuoreum.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jeong.jejuoreum.core.designsystem.theme.JejuOreumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JejuOreumScaffold(
    modifier: Modifier = Modifier,
    titleRes: Int,
    isDarkTheme: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    JejuOreumTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = titleRes)) },
                    colors = TopAppBarDefaults.topAppBarColors()
                )
            },
            content = content
        )
    }
}
