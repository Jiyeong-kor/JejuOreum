package com.jeong.jjoreum.presentation.ui.profile

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.jeong.jjoreum.databinding.FragmentMyBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment

/**
 * 마이페이지(내 정보) 관련 화면을 구성하는 Fragment
 * 내부에 ViewPager2로 즐겨찾기, 스탬프 등 탭을 관리함
 */
class MyFragment : ViewBindingBaseFragment<FragmentMyBinding>(FragmentMyBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagerAndTabs()
    }

    /**
     * ViewPager와 TabLayout을 설정
     */
    private fun setupViewPagerAndTabs() = binding?.apply {
        val adapter = MyViewPagerAdapter(this@MyFragment)
        viewPagerMy.adapter = adapter

        TabLayoutMediator(tabLayoutMy, viewPagerMy) { tab, position ->
            tab.text = adapter.getPageTitle(position) // 각 탭의 제목 설정
        }.attach()
    }
}