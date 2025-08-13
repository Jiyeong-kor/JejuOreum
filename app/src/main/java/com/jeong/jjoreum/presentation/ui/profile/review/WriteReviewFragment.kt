package com.jeong.jjoreum.presentation.ui.profile.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.WriteReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteReviewFragment : Fragment() {

    private val viewModel: WriteReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val oreumIdx = arguments?.getInt("oreumIdx", -1) ?: -1
        val oreumName = arguments?.getString("oreumName") ?: "오름 정보 없음"
        viewModel.init(oreumIdx = oreumIdx, oreumName = oreumName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JJOreumTheme {
                    WriteReviewRoute(viewModel = viewModel)
                }
            }
        }
    }
}