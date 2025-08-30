package com.jeong.jjoreum.presentation.ui.profile.stamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyStampFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MyStampScreen { oreumIdx, oreumName ->
                val bundle = bundleOf(
                    "oreumIdx" to oreumIdx,
                    "oreumName" to oreumName
                )
                findNavController().navigate(R.id.writeReviewFragment, bundle)
            }
        }
    }
}