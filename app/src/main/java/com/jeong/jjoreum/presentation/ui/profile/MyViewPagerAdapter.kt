package com.jeong.jjoreum.presentation.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.profile.favorite.MyFavoriteFragment
import com.jeong.jjoreum.presentation.ui.profile.stamp.MyStampFragment

/**
 * 마이페이지의 ViewPager 어댑터
 * 즐겨찾기(Favorite), 스탬프(Stamp) 탭을 관리함
 */
class MyViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment), TabLayoutMediator.TabConfigurationStrategy {

    private val context = fragment.requireContext()

    private val fragmentLaunchers: List<() -> Fragment> = listOf(
        { MyFavoriteFragment() },
        { MyStampFragment() }
    )

    // 탭에 표시할 문자열 리소스 ID 리스트
    private val titlesRes = listOf(
        R.string.tab_favorites,
        R.string.tab_stamps
    )

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = tabTitles(position)
    }

    override fun getItemCount() = fragmentLaunchers.size

    override fun createFragment(position: Int): Fragment = fragmentLaunchers[position].invoke()

    /**
     * 각 페이지(탭)의 제목을 가져옴
     * @param position 탭 위치
     * @return 탭 제목 문자열
     */
    fun getPageTitle(position: Int): String = tabTitles(position)

    /**
     * 탭에 표시할 문자열을 반환
     * @param position 탭 인덱스
     */
    private fun tabTitles(position: Int): String =
        context.getString(titlesRes[position])
}