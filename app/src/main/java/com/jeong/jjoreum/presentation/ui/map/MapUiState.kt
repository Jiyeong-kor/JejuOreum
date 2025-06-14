package com.jeong.jjoreum.presentation.ui.map

import com.jeong.jjoreum.data.model.api.ResultSummary


/**
 * 지도 화면 UI 상태 정의
 * - 상태 기반으로 검색 결과, 결과 없음, 숨김 여부 결정
 *
 * @constructor Create empty Map ui state
 */
sealed class MapUiState {

    // 초기 상태
    data object Idle : MapUiState()

    // 결과 있음
    data class SearchResults(val list: List<ResultSummary>) : MapUiState()

    // 결과 없음
    data object NoResults : MapUiState()

    // 검색창/결과 숨김
    data object Hidden : MapUiState()
}