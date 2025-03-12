package com.jeong.jjoreum.presentation.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.util.toastMessage
import com.jeong.jjoreum.databinding.ActivityJoinFormBinding
import com.jeong.jjoreum.presentation.viewmodel.JoinViewModel
import com.jeong.jjoreum.presentation.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * 회원가입(Join) 화면의 폼을 처리하는 Activity
 */
class JoinFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinFormBinding
    // JoinViewModel 주입
    private val joinViewModel: JoinViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JoinViewModel(application) as T
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinFormBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        setupUI()
        observeViewModel()
    }

    /**
     * UI 설정 및 이벤트 리스너 등록
     */
    @SuppressLint("CheckResult")
    private fun setupUI() {
        // 닉네임 입력 시 디바운스를 통해 일정 시간 후 검증 진행
        binding.joinFormEditNickname.textChanges()
            .debounce(700, TimeUnit.MILLISECONDS)
            .subscribe { text ->
                joinViewModel.updateNickname(text.toString())
            }

        // 다음 버튼 클릭 시 닉네임 저장 로직 호출
        binding.joinBtnNext.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe { saveNickname() }

        // 뒤로가기 버튼 클릭 시 액티비티 종료
        binding.joinFormBack.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe { finish() }
    }

    /**
     * ViewModel 관찰
     */
    private fun observeViewModel() {
        // 닉네임 변경 관찰
        lifecycleScope.launch {
            joinViewModel.nickname.collectLatest { nickname ->
                binding.joinFormEditNickname.setText(nickname)
            }
        }

        // 닉네임 에러 메시지 관찰
        lifecycleScope.launch {
            joinViewModel.nicknameError.collectLatest { error ->
                binding.joinFormLayoutNickname.error = error
                binding.joinAvailableMessage.visibility = if (error == null) View.VISIBLE else View.GONE
            }
        }

        // 닉네임 사용 가능 여부 관찰
        lifecycleScope.launch {
            joinViewModel.isNicknameAvailable.collectLatest { isAvailable ->
                binding.joinBtnNext.isEnabled = isAvailable
            }
        }

        // 닉네임 에러 메시지 (중복 등) 관찰
        lifecycleScope.launch {
            joinViewModel.nicknameErrorMessage.collectLatest { errorMessage ->
                if (errorMessage != null) {
                    binding.joinErrorMessage.text = errorMessage
                    binding.joinErrorMessage.visibility = View.VISIBLE
                    binding.joinAvailableMessage.visibility = View.GONE
                } else {
                    binding.joinErrorMessage.visibility = View.GONE
                    binding.joinAvailableMessage.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * 닉네임을 Firestore에 저장하는 함수
     */
    private fun saveNickname() {
        joinViewModel.saveNickname(
            onSuccess = { nickname ->
                // 닉네임 저장 성공 시
                toastMessage("닉네임이 저장되었습니다!")

                val prefs = PreferenceManager.getInstance(this)
                prefs.setNickname(nickname)

                // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            },
            onFailure = {
                // 닉네임 저장 실패 시
                toastMessage("닉네임 저장 실패")
            }
        )
    }
}
