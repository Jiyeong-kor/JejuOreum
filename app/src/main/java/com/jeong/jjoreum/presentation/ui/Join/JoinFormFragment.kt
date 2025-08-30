package com.jeong.jjoreum.presentation.ui.Join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.presentation.ui.main.MainActivity
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.JoinViewModel
import com.jeong.jjoreum.util.toastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class JoinFormFragment : Fragment() {

    private val joinViewModel: JoinViewModel by viewModels()

    private val _isTermCheckBoxChecked = MutableStateFlow(false)

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) signInAnonymously()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    val termChecked by _isTermCheckBoxChecked.collectAsState()
                    JoinFormScreen(
                        viewModel = joinViewModel,
                        isTermChecked = termChecked,
                        onTermCheckedChange = { _isTermCheckBoxChecked.value = it },
                        onTermClick = {
                            JoinTermDialogFragment.newInstance()
                                .show(childFragmentManager, JoinTermDialogFragment.TAG)
                        },
                        onNextClick = { saveNickname() }
                    )
                }
            }
        }
    }

    private fun signInAnonymously() {
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(requireActivity()) { task ->
                if (!task.isSuccessful) {
                    toastMessage(getString(R.string.auth_failed))
                    findNavController().popBackStack()
                }
            }
    }

    private fun saveNickname() {
        joinViewModel.saveNickname(
            onSuccess = { nickname ->
                toastMessage(getString(R.string.signup_success))
                val prefs = PreferenceManager(requireContext())
                prefs.setNickname(nickname)
                requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .edit {
                        putBoolean("is_signed_up", true)
                    }
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            },
            onFailure = {
                toastMessage(getString(R.string.nickname_save_failed))
            }
        )
    }
}