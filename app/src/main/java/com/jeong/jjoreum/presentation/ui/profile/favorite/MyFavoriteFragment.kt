package com.jeong.jjoreum.presentation.ui.profile.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.MyFavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFavoriteFragment : Fragment() {

    private val viewModel: MyFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    MyFavoriteScreen(
                        viewModel = viewModel,
                        onItemClick = { oreum ->
                            val bundle = Bundle().apply {
                                putParcelable("oreumData", oreum)
                            }
                            findNavController().navigate(R.id.detailFragment, bundle)
                        }
                    )
                }
            }
        }
    }
}