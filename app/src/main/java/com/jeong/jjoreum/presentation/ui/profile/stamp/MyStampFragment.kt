package com.jeong.jjoreum.presentation.ui.profile.stamp

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.FragmentMyStampBinding
import com.jeong.jjoreum.data.model.entity.MyStampItem
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * '나의 스탬프' 탭을 표시하는 Fragment
 * 사용자가 획득한 스탬프 목록을 보여줌
 */
class MyStampFragment :
    ViewBindingBaseFragment<FragmentMyStampBinding>(FragmentMyStampBinding::inflate) {

    // 사용자의 스탬프 정보를 저장하는 리스트
    private val stampList = mutableListOf<MyStampItem>()
    private lateinit var listAdapter: MyStampAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateUI()
        observeItemClicks()
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView() {
        listAdapter = MyStampAdapter()

        binding?.recyclerViewStampList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    /**
     * UI 갱신
     * 스탬프 목록이 비어있으면 안내 문구를 표시
     */
    private fun updateUI() {
        val isEmpty = stampList.isEmpty()
        binding?.apply {
            textViewEmptyStampList.visibility = if (isEmpty) View.VISIBLE else View.GONE
            recyclerViewStampList.visibility = if (isEmpty) View.GONE else View.VISIBLE

            if (::listAdapter.isInitialized && stampList.isNotEmpty()) {
                listAdapter.submitList(stampList)
            }
        }
    }

    /**
     * 스탬프 아이템 클릭 이벤트를 관찰
     */
    private fun observeItemClicks() {
        viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.itemClickFlow
                .collectLatest {
                    findNavController().navigate(R.id.action_listFragment_to_detailFragment)
                }
        }
    }
}
