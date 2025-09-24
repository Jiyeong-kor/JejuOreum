package com.jeong.jjoreum.presentation.ui.Join

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.databinding.FragmentJoinFormBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.ui.main.MainActivity
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.JoinViewModel
import com.jeong.jjoreum.util.toastMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class JoinFormFragment :
    ViewBindingBaseFragment<FragmentJoinFormBinding>(FragmentJoinFormBinding::inflate) {

    private lateinit var joinViewModel: JoinViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private var buttonClickJob: Job? = null

    private var colorOnSurfaceVariantAttr: Int = 0
    private var colorErrorAttr: Int = 0
    private var colorPrimaryAttr: Int = 0

    private var isKeyboardVisible = false
    private lateinit var rootView: View

    private val _isTermCheckBoxChecked = MutableStateFlow(false)
    val isTermCheckBoxChecked: StateFlow<Boolean> get() = _isTermCheckBoxChecked

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = binding?.root ?: return

        val context = requireContext().applicationContext
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val prefs = PreferenceManager(context)

        val factory = AppViewModelFactory(
            prefs = prefs,
            firestore = firestore,
            auth = auth,
            context = context
        )

        joinViewModel = ViewModelProvider(this, factory)[JoinViewModel::class.java]
        firebaseAuth = auth

        // 컬러 속성 초기화
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            typedValue, true
        )
        colorOnSurfaceVariantAttr = typedValue.data
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnError,
            typedValue,
            true
        )
        colorErrorAttr = typedValue.data
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimary,
            typedValue,
            true
        )
        colorPrimaryAttr = typedValue.data

        if (firebaseAuth.currentUser == null) {
            signInAnonymously()
        } else {
            setupUI()
            observeViewModel()
            setupKeyboardVisibilityListener()
            setupTermCheckboxListener()
        }
    }

    override fun onDestroyView() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
        super.onDestroyView()
    }

    private fun signInAnonymously() {
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    setupUI()
                    observeViewModel()
                    setupKeyboardVisibilityListener()
                    setupTermCheckboxListener()
                } else {
                    toastMessage(getString(R.string.auth_failed))
                    findNavController().popBackStack()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun setupUI() {
        with(binding!!) {
            joinFormEditNickname.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val nickname = joinFormEditNickname.text.toString().trim()
                    joinViewModel.updateNickname(nickname)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    joinFormEditNickname.clearFocus()
                    true
                } else false
            }

            joinFormEditNickname.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val nickname = joinFormEditNickname.text.toString().trim()
                    if (nickname.isNotEmpty() || joinViewModel.hasUserTyped.value) {
                        joinViewModel.updateNickname(nickname)
                    }
                }
            }

            joinBtnNext.setOnClickListener {
                if (buttonClickJob?.isActive == true) return@setOnClickListener
                buttonClickJob = viewLifecycleOwner.lifecycleScope.launch {
                    if (joinBtnNext.isEnabled && !joinViewModel.isLoadingNicknameAvailability.value && _isTermCheckBoxChecked.value) {
                        saveNickname()
                    } else if (joinViewModel.isLoadingNicknameAvailability.value) {
                        toastMessage("닉네임 확인 중입니다. 잠시만 기다려주세요.")
                    } else if (!_isTermCheckBoxChecked.value) {
                        toastMessage("약관 동의가 필요합니다.")
                    }
                    delay(1000L)
                }
            }

            termArrow.setOnClickListener {
                JoinTermDialogFragment.newInstance()
                    .show(childFragmentManager, JoinTermDialogFragment.TAG)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            joinViewModel.isNicknameInvalid.collectLatest { isInvalid ->
                val hasTyped = joinViewModel.hasUserTyped.value
                binding?.joinNicknameRule?.setTextColor(
                    if (hasTyped && isInvalid) colorErrorAttr else colorOnSurfaceVariantAttr
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            joinViewModel.isLoadingNicknameAvailability.collectLatest { isLoading ->
                binding?.progressBarNicknameCheck?.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                joinViewModel.nickname,
                joinViewModel.hasUserTyped,
                joinViewModel.isNicknameAvailable,
                joinViewModel.isNicknameInvalid,
                joinViewModel.nicknameErrorMessage
            ) { currentNickname, hasTyped, isAvailable, isInvalid, firestoreErrorMessage ->
                val nicknameNotEmpty = currentNickname.trim().isNotEmpty()

                if (!hasTyped && !nicknameNotEmpty) {
                    NicknameStatusUiState(View.VISIBLE, View.GONE, null, colorOnSurfaceVariantAttr)
                } else if (isInvalid) {
                    NicknameStatusUiState(View.VISIBLE, View.GONE, null, colorErrorAttr)
                } else if (!isAvailable) {
                    NicknameStatusUiState(
                        View.GONE,
                        View.VISIBLE,
                        firestoreErrorMessage,
                        colorErrorAttr
                    )
                } else if (isAvailable && nicknameNotEmpty && !isInvalid) {
                    NicknameStatusUiState(
                        View.GONE,
                        View.VISIBLE,
                        getString(R.string.nickname_available),
                        colorPrimaryAttr
                    )
                } else {
                    NicknameStatusUiState(View.VISIBLE, View.GONE, null, colorOnSurfaceVariantAttr)
                }
            }.collectLatest { uiState ->
                binding?.apply {
                    joinNicknameRule.visibility = uiState.ruleVisibility
                    joinStatusMessage.visibility = uiState.statusMessageVisibility
                    joinStatusMessage.text = uiState.statusMessageText
                    joinStatusMessage.setTextColor(uiState.statusMessageTextColor)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                joinViewModel.isNicknameAvailable,
                joinViewModel.nickname,
                joinViewModel.isNicknameInvalid,
                joinViewModel.isLoadingNicknameAvailability,
                _isTermCheckBoxChecked
            ) { isAvailable, nicknameValue, isInvalid, isLoading, isTermChecked ->
                isAvailable && nicknameValue.trim()
                    .isNotEmpty() && !isInvalid && !isLoading && isTermChecked
            }.collectLatest { isEnabled ->
                binding?.joinBtnNext?.isEnabled = isEnabled
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            _isTermCheckBoxChecked.collectLatest { /* 필요시 처리 */ }
        }
    }

    private fun saveNickname() {
        joinViewModel.saveNickname(
            onSuccess = { nickname ->
                toastMessage(getString(R.string.signup_success))
                val prefs = PreferenceManager(requireContext())
                prefs.setNickname(nickname)

                requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .edit { putBoolean("is_signed_up", true) }

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            },
            onFailure = {
                toastMessage(getString(R.string.nickname_save_failed))
            }
        )
    }

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.rootView.height
        val keypadHeight = screenHeight - r.bottom
        val pxThreshold = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            150f,
            resources.displayMetrics
        ).toInt()
        val wasKeyboardVisible = isKeyboardVisible
        isKeyboardVisible = keypadHeight > pxThreshold

        if (wasKeyboardVisible && !isKeyboardVisible) {
            if (binding?.joinFormEditNickname?.hasFocus() == true) {
                val nickname = binding?.joinFormEditNickname?.text.toString().trim()
                joinViewModel.updateNickname(nickname)
            }
        }
    }

    private fun setupKeyboardVisibilityListener() {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    private fun setupTermCheckboxListener() {
        binding?.termCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            _isTermCheckBoxChecked.value = isChecked
        }
    }

    data class NicknameStatusUiState(
        val ruleVisibility: Int,
        val statusMessageVisibility: Int,
        val statusMessageText: String?,
        val statusMessageTextColor: Int
    )
}