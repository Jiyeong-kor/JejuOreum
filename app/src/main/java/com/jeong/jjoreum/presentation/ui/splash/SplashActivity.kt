package com.jeong.jjoreum.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.presentation.ui.login.LoginActivity
import com.jeong.jjoreum.presentation.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 앱 실행 시 최초로 보여지는 스플래시 화면 액티비티
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 스플래시 이미지 로드
        val splashImage = findViewById<ImageView>(R.id.splash_image)
        splashImage.load(R.drawable.oreum) {
            size(ViewSizeResolver(splashImage))
            transformations(RoundedCornersTransformation(16f))
            memoryCachePolicy(CachePolicy.ENABLED)
        }

        // SharedPreferences를 통해 사용자 등록 여부 판단
        val prefs = PreferenceManager.getInstance(this)

        // 일정 시간(2초) 대기 후 다음 액티비티로 이동
        lifecycleScope.launch {
            delay(2000)
            val nextActivity = if (prefs.isUserRegistered()) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }
    }
}
