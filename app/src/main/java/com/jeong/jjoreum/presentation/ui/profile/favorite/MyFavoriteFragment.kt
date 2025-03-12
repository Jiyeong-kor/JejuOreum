package com.jeong.jjoreum.presentation.ui.profile.favorite

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.FragmentMyFavoriteBinding
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.ui.list.OreumListAdapter
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * '즐겨찾기' 탭을 표시하는 Fragment
 * 사용자가 즐겨찾기한 오름 목록을 보여줌
 */
class MyFavoriteFragment :
    ViewBindingBaseFragment<FragmentMyFavoriteBinding>(FragmentMyFavoriteBinding::inflate) {

    // 사용자가 즐겨찾기한 오름 목록
    private val favoriteOreumList = mutableListOf<ResultSummary>()

    // RecyclerView 어댑터
    private var listAdapter = OreumListAdapter { oreum ->
        navigateToDetailFragment(oreum)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateUI()
        observeItemClicks()
    }

    /**
     * RecyclerView 초기 설정
     */
    private fun setupRecyclerView() {
        binding?.recyclerViewFavoriteList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    /**
     * UI를 갱신
     * 즐겨찾기 목록이 비어있으면 안내 문구를 표시
     */
    private fun updateUI() {
        val isEmpty = favoriteOreumList.isEmpty()
        binding?.apply {
            textViewEmptyFavoriteList.visibility = if (isEmpty) View.VISIBLE else View.GONE
            recyclerViewFavoriteList.visibility = if (isEmpty) View.GONE else View.VISIBLE

            if (binding?.recyclerViewFavoriteList?.adapter != null && favoriteOreumList.isNotEmpty()) {
                listAdapter.submitList(favoriteOreumList)
            }
        }
    }

    /**
     * 어댑터의 아이템 클릭 이벤트를 Flow로 관찰
     */
    private fun observeItemClicks() {
        viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.itemClickFlow
                .debounce(500)
                .collect {
                    navigateToDetailFragment(it)
                }
        }
    }

    /**
     * 상세화면으로 이동
     * @param oreum 선택된 오름 정보
     */
    private fun navigateToDetailFragment(oreum: ResultSummary) {
        val bundle = Bundle().apply {
            putParcelable("oreumData", oreum)
        }
        findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
    }
}
