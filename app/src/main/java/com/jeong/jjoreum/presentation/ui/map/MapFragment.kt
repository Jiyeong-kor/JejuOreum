package com.jeong.jjoreum.presentation.ui.map

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.databinding.FragmentMapBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment :
    ViewBindingBaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {

    private val mapViewModel: MapViewModel by viewModels()
    private var mapController: MapController? = null
    private var ignoreTextChanges = false

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) = with(binding!!) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (searchResultContainer.isVisible || textViewNoResults.isVisible) {
                hideSearchResults()
            } else {
                requireActivity().finishAffinity()
            }
        }

        val searchResultAdapter = SearchResultAdapter { result ->
            val latLng = LatLng.from(result.y, result.x)
            mapController?.let {
                it.highlightMarker(latLng)
                it.moveCameraTo(latLng)
            }
            mapViewModel.selectOreum(latLng)
            findNavController().navigate(
                MapFragmentDirections.actionMapFragmentToDetailFragment(
                    result
                )
            )
            hideSearchResults()
            hideKeyboardAndClearFocus()
        }
        searchResultList.adapter = searchResultAdapter

        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (!ignoreTextChanges) {
                mapViewModel.onSearchQueryChanged(text?.toString() ?: "")
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mapViewModel.onSearchQueryChanged(searchEditText.text.toString())
                hideKeyboardAndClearFocus()
                true
            } else false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                mapViewModel.uiState.collect { state ->
                    when (state) {
                        is MapUiState.SearchResults -> {
                            searchResultAdapter.searchResults = state.list
                            searchResultContainer.post {
                                searchResultContainer.visibility = View.VISIBLE
                                searchResultList.visibility = View.VISIBLE
                                noResultContainer.visibility = View.GONE
                            }
                        }

                        is MapUiState.NoResults -> {
                            searchResultAdapter.searchResults = emptyList()
                            searchResultContainer.post {
                                searchResultContainer.visibility = View.VISIBLE
                                searchResultList.visibility = View.GONE
                                noResultContainer.visibility = View.VISIBLE
                            }
                        }

                        is MapUiState.Hidden, is MapUiState.Idle -> {
                            searchResultAdapter.searchResults = emptyList()
                            searchResultContainer.visibility = View.GONE
                            noResultContainer.visibility = View.GONE
                        }
                    }
                }
            }
        }

        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(error: Exception) {
                    Log.e("MapFragment", "Map error: ${error.message}")
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    mapController = MapController(map) { latLng ->
                        mapController?.highlightMarker(latLng)
                        mapViewModel.onPoiClicked(latLng)?.let {
                            OreumInfoBottomSheetDialogFragment.newInstance(it)
                                .show(childFragmentManager, "oreum_info")
                        }
                    }

                    lifecycleScope.launch {
                        repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                            mapViewModel.oreumList.collect {
                                mapController?.drawOreumMarkers(it)
                            }
                        }
                    }

                    mapController?.drawOreumMarkers(mapViewModel.oreumList.value)

                    map.setOnViewportClickListener { _, _, _ ->
                        hideSearchResults()
                        mapViewModel.onMapTouched()
                    }
                }
            }
        )

        childFragmentManager.setFragmentResultListener(
            "navigateToDetail",
            viewLifecycleOwner
        ) { _, bundle ->
            val oreum = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("oreum", ResultSummary::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable("oreum")
            }

            oreum?.let {
                findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToDetailFragment(
                        it
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            mapViewModel.oreumList.collect {
                mapController?.drawOreumMarkers(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapController = null
    }

    private fun hideKeyboardAndClearFocus() = with(binding!!) {
        val imm = ContextCompat.getSystemService(
            requireContext(), InputMethodManager::class.java
        )
        imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }

    private fun hideSearchResults() = with(binding!!) {
        mapViewModel.hideSearch()
        ignoreTextChanges = true
        searchEditText.setText("")
        searchEditText.post { ignoreTextChanges = false }

        searchResultContainer.visibility = View.GONE
        searchResultList.visibility = View.GONE
        noResultContainer.visibility = View.GONE
        textViewNoResults.visibility = View.GONE

        hideKeyboardAndClearFocus()
    }
}