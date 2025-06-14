package com.jeong.jjoreum.presentation.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import com.jeong.jjoreum.databinding.ActivityMainBinding
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.SplashViewModel
import com.jeong.jjoreum.repository.OreumRepositoryImpl

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        val prefs = PreferenceManager(applicationContext)
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val apiService = RetrofitOkHttpManager.oreumRetrofitBuilder
            .create(OreumRetrofitInterface::class.java)

        val oreumRepository = OreumRepositoryImpl(firestore, auth, apiService)
        val factory = AppViewModelFactory(
            prefs = prefs,
            oreumRepository = oreumRepository,
            context = applicationContext
        )
        viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkUserStatus()

        val navController = (supportFragmentManager
            .findFragmentById(R.id.container_main) as NavHostFragment).navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility = when (destination.id) {
                R.id.splashFragment, R.id.joinFormFragment -> View.GONE
                else -> View.VISIBLE
            }
        }
    }
}