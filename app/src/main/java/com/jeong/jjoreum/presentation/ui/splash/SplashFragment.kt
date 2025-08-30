package com.jeong.jjoreum.presentation.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    SplashScreen()
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is SplashUiState.GoToMap -> findNavController().navigate(
                    R.id.action_splash_to_map,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build(),
                )

                is SplashUiState.GoToJoin ->
                    findNavController().navigate(R.id.action_splash_to_join)
            }
        }

        viewModel.checkUserStatus()
    }
}