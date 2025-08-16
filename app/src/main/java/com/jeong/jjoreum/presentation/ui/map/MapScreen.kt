package com.jeong.jjoreum.presentation.ui.map

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToDetail: (ResultSummary) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visibleList by viewModel.visibleOreumList.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current

    var sheetOreum by remember { mutableStateOf<ResultSummary?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var mapController by remember { mutableStateOf<MapController?>(null) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember(context) { MapView(context) }

    BackHandler(enabled = uiState is MapUiState.SearchResults || uiState is MapUiState.NoResults) {
        viewModel.hideSearch(); query = ""; focus.clearFocus()
    }

    LaunchedEffect(visibleList, mapController) {
        mapController?.drawOreumMarkers(visibleList)
    }

    Box(Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.apply {
                    isFinishManually = true
                    start(
                        object : MapLifeCycleCallback() {
                            override fun onMapDestroy() {}
                            override fun onMapError(error: Exception) {
                                Log.e("MapScreen", "Map error: ${error.message}")
                            }
                        },
                        object : KakaoMapReadyCallback() {
                            override fun onMapReady(map: KakaoMap) {
                                mapController = MapController(map) { latLng ->
                                    mapController?.highlightMarker(latLng)
                                    mapController?.moveCameraTo(latLng)
                                    val oreum = viewModel.onPoiClicked(latLng)
                                    if (oreum != null) {
                                        sheetOreum = oreum; showSheet = true
                                    }
                                    viewModel.onMapTouched()
                                }

                                map.setOnViewportClickListener { _, _, _ ->
                                    viewModel.hideSearch(); query = ""; focus.clearFocus()
                                }

                                map.setOnCameraMoveEndListener { currentMap, _, _ ->
                                    val vp = currentMap.viewport
                                    val sw = currentMap.fromScreenPoint(vp.left, vp.bottom)
                                    val ne = currentMap.fromScreenPoint(vp.right, vp.top)
                                    if (sw != null && ne != null) {
                                        viewModel.loadOreumForVisibleArea(
                                            LatLngBounds(ne, sw)
                                        )
                                    }
                                }

                                val hallasan = LatLng.from(33.3616, 126.5312)
                                map.moveCamera(
                                    CameraUpdateFactory
                                        .newCenterPosition(hallasan, 10)
                                )
                            }
                        }
                    )
                }
            }
        )

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.onSearchQueryChanged(it)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("오름의 이름이나 주소로 검색해 주세요") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.onSearchQueryChanged(query)
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

            when (val state = uiState) {
                is MapUiState.SearchResults -> {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        SearchResultsList(
                            list = state.list,
                            onClick = { item ->
                                val latLng = LatLng.from(item.y, item.x)
                                mapController?.highlightMarker(latLng)
                                mapController?.moveCameraTo(latLng)
                                onNavigateToDetail(item)
                                query = ""
                                viewModel.hideSearch()
                                focus.clearFocus()
                            }
                        )
                    }
                }

                is MapUiState.NoResults -> {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        NoResultsCard()
                    }
                }

                is MapUiState.Hidden, is MapUiState.Idle -> Unit
            }
        }

        if (showSheet && sheetOreum != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = Color.White
            ) {
                OreumSheetContent(
                    oreum = sheetOreum!!,
                    onClickDetail = {
                        showSheet = false
                        onNavigateToDetail(sheetOreum!!)
                    }
                )
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapController = null
            mapView.finish()
        }
    }
}

@Composable
private fun SearchResultsList(
    list: List<ResultSummary>,
    onClick: (ResultSummary) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(list) { item ->
            SearchResultItem(item, onClick)
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        }
    }
}

@Composable
private fun SearchResultItem(
    item: ResultSummary,
    onClick: (ResultSummary) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick(item) }
            .padding(12.dp)
    ) {
        AsyncImage(
            model = item.imgPath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.oreumKname,
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.oreumAddr,
            fontSize = 14.sp,
            color = Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun NoResultsCard() {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text("검색 결과가 없어요.") }
    }
}

@Composable
private fun OreumSheetContent(
    oreum: ResultSummary,
    onClickDetail: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClickDetail() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(oreum.imgPath)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.error_image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = oreum.oreumKname,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = oreum.oreumAddr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}