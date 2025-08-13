package com.jeong.jjoreum.presentation.ui.profile

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.jeong.jjoreum.databinding.FragmentMyBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFragment : ViewBindingBaseFragment<FragmentMyBinding>(FragmentMyBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagerAndTabs()
    }

    private fun setupViewPagerAndTabs() = binding?.apply {
        val adapter = MyViewPagerAdapter(this@MyFragment)
        viewPagerMy.adapter = adapter

        TabLayoutMediator(tabLayoutMy, viewPagerMy) { tab, position ->
            // 각 탭의 제목 설정
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}