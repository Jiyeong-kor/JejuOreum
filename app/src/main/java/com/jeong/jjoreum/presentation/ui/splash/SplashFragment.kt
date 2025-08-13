package com.jeong.jjoreum.presentation.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.FragmentSplashBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment :
    ViewBindingBaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is SplashUiState.GoToMap -> findNavController().navigate(
                    R.id.action_splash_to_map,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build()
                )

                is SplashUiState.GoToJoin ->
                    findNavController().navigate(R.id.action_splash_to_join)
            }
        }

        viewModel.checkUserStatus()
    }
}