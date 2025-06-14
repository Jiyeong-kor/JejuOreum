package com.jeong.jjoreum.presentation.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import com.jeong.jjoreum.databinding.FragmentSplashBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import com.jeong.jjoreum.repository.OreumRepositoryImpl

class SplashFragment :
    ViewBindingBaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private lateinit var viewModel: SplashViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appContext = requireContext().applicationContext
        val prefs = PreferenceManager(appContext)
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val apiService = RetrofitOkHttpManager.oreumRetrofitBuilder
            .create(OreumRetrofitInterface::class.java)
        val oreumRepository = OreumRepositoryImpl(firestore, auth, apiService)

        val factory = AppViewModelFactory(
            prefs = prefs,
            oreumRepository = oreumRepository,
            context = appContext
        )
        viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is SplashUiState.GoToMap -> findNavController().navigate(
                    R.id.action_splash_to_map,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build()
                )

                is SplashUiState.GoToJoin -> findNavController().navigate(R.id.action_splash_to_join)
            }
        }

        viewModel.checkUserStatus()
    }
}