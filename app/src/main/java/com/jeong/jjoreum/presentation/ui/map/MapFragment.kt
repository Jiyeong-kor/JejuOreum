package com.jeong.jjoreum.presentation.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            MapScreen(
                onNavigateToWriteReview = { oreumIdx, oreumName ->
                    findNavController().navigate(
                        R.id.writeReviewFragment,
                        bundleOf("oreumIdx" to oreumIdx, "oreumName" to oreumName)
                    )
                },
                onFavoriteToggled = { oreumIdx ->
                    setFragmentResult(
                        "oreum_update",
                        bundleOf(
                            "shouldRefresh" to true,
                            "oreumIdx" to (oreumIdx.toIntOrNull() ?: -1)
                        )
                    )
                }
            )
        }
    }
}