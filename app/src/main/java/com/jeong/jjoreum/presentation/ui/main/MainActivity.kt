package com.jeong.jjoreum.presentation.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.ActivityMainBinding

/**
 * 앱의 메인 액티비티
 * BottomNavigationView를 통해 주요 화면들을 이동함
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding 객체
    private lateinit var viewBinding: ActivityMainBinding

    // NavController (public으로 선언하여 외부에서도 사용 가능)
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 설정
        viewBinding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        // 네비게이션 설정
        setupNavigation()
    }

    /**
     * NavHostFragment와 BottomNavigationView를 연동하는 함수
     */
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_main) as? NavHostFragment
        navController = navHostFragment?.navController ?: return

        val bottomNav = viewBinding.navigationMain

        // BottomNavigationView와 NavController 연결
        bottomNav.setupWithNavController(navController)

        // 메뉴 항목 클릭 시 중복으로 같은 화면을 띄우지 않도록 처리
        bottomNav.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id
            if (item.itemId != currentDestination) {
                navController.navigate(item.itemId)
            }
            true
        }
    }

    /**
     * 네비게이션 Up 버튼 동작 재정의
     */
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()
}
