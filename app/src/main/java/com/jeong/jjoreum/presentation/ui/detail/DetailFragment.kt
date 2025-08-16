package com.jeong.jjoreum.presentation.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val oreumData = DetailFragmentArgs.fromBundle(requireArguments()).oreumData
        viewModel.setOreumDetail(oreumData)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    DetailScreen(
                        viewModel = viewModel,
                        onNavigateToWriteReview = { oreumIdx, oreumName ->
                            val action =
                                DetailFragmentDirections.actionDetailFragmentToWriteReviewFragment(
                                    oreumIdx, oreumName
                                )
                            findNavController().navigate(action)
                        },
                        showToast = { message ->
                            Toast.makeText(
                                requireContext(), message,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onFavoriteToggled = { oreumIdx ->
                            setFragmentResult("oreum_update", Bundle().apply {
                                putBoolean("shouldRefresh", true)
                                putInt("oreumIdx", oreumIdx.toInt())
                            })
                        }
                    )
                }
            }
        }
    }
}