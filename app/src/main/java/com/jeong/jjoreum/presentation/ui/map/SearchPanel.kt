package com.jeong.jjoreum.presentation.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary

@Composable
fun SearchPanel(
    query: String,
    uiState: MapUiState,
    onQueryChange: (String) -> Unit,
    onResultClick: (ResultSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    val focus = LocalFocusManager.current

    Column(modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onQueryChange(query)
                    focus.clearFocus()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                errorContainerColor = Color.White
            )
        )

        when (uiState) {
            is MapUiState.SearchResults -> {
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        items(uiState.list, key = { "${it.x},${it.y}" }) { item ->
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .clickable { onResultClick(item) }
                                    .padding(12.dp)
                            ) {
                                AsyncImage(
                                    model = item.imgPath,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    item.oreumKname,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    item.oreumAddr,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            HorizontalDivider(
                                thickness = DividerDefaults.Thickness,
                                color = DividerDefaults.color
                            )
                        }
                    }
                }
            }

            is MapUiState.NoResults -> {
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) { Text(stringResource(id = R.string.search_empty)) }
            }

            is MapUiState.Hidden, is MapUiState.Idle -> Unit
        }
    }
}
