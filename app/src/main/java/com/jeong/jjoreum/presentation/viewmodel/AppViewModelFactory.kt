package com.jeong.jjoreum.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.presentation.ui.map.MapViewModel
import com.jeong.jjoreum.repository.*

class AppViewModelFactory(
    private val oreumRepository: OreumRepository? = null,
    private val interactionRepository: UserInteractionRepository? = null,
    private val stampRepository: StampRepository? = null,
    private val reviewRepository: ReviewRepository? = null,
    private val prefs: PreferenceManager? = null,
    private val auth: FirebaseAuth? = null,
    private val firestore: FirebaseFirestore? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(interactionRepository!!, reviewRepository!!, stampRepository!!) as T
            }
            modelClass.isAssignableFrom(JoinViewModel::class.java) -> {
                JoinViewModel(prefs!!, firestore!!, auth!!) as T
            }
            modelClass.isAssignableFrom(ListViewModel::class.java) -> {
                ListViewModel(oreumRepository!!, interactionRepository!!, stampRepository!!) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(oreumRepository!!) as T
            }
            modelClass.isAssignableFrom(MyFavoriteViewModel::class.java) -> {
                MyFavoriteViewModel(oreumRepository as OreumRepositoryImpl) as T
            }
            modelClass.isAssignableFrom(MyStampViewModel::class.java) -> {
                MyStampViewModel(oreumRepository!!, interactionRepository!!) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(prefs!!, oreumRepository!!, context!!) as T
            }
            modelClass.isAssignableFrom(WriteReviewViewModel::class.java) -> {
                WriteReviewViewModel(reviewRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
