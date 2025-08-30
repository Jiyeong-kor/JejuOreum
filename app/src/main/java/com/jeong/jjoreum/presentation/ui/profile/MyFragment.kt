package com.jeong.jjoreum.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            JJOreumTheme {
                MyScreen(
                    onFavoriteItemClick = { oreum ->
                        val bundle = bundleOf("oreumData" to oreum)
                        findNavController().navigate(R.id.detailFragment, bundle)
                    },
                    onNavigateToWriteReview = { oreumIdx, oreumName ->
                        val bundle = bundleOf(
                            "oreumIdx" to oreumIdx,
                            "oreumName" to oreumName
                        )
                        findNavController().navigate(R.id.writeReviewFragment, bundle)
                    }
                )
            }
        }
    }
}