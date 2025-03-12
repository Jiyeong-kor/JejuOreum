package com.jeong.jjoreum.presentation.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 앱 실행 시, 권한이 필요한 경우 이를 확인 및 요청하는 액티비티
 */
class PermissionActivity : AppCompatActivity() {

    private lateinit var permissionCheck: PermissionCheck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionCheck = PermissionCheck(this)

        if (permissionCheck.isPermission) {
            // 이미 권한이 허용됨 → 메인 화면 이동
            navigateToMain()
        } else {
            // 권한 요청 로직 실행
            requestPermissions()
        }
    }

    /**
     * 메인 화면으로 이동하는 함수
     * 실제 구현체는 생략됨
     */
    private fun navigateToMain() {
        // MainActivity로 이동하는 로직 추가
    }

    /**
     * 권한을 요청하는 함수
     * 실제 구현체는 생략됨
     */
    private fun requestPermissions() {
        // 권한 요청 처리
    }
}
