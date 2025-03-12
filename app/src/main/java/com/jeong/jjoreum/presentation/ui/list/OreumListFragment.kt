package com.jeong.jjoreum.presentation.ui.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.FragmentOreumListBinding
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.OreumListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 메인 화면(바텀 탭 중)에서 오름 리스트를 표시하는 Fragment
 * 오름 리스트를 서버에서 가져와 RecyclerView로 보여주고,
 * 각 오름을 선택했을 때 상세 화면으로 이동함
 */
class OreumListFragment :
    ViewBindingBaseFragment<FragmentOreumListBinding>(FragmentOreumListBinding::inflate) {

    private lateinit var viewModel: OreumListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[OreumListViewModel::class.java]
    }

    // RecyclerView Adapter
    private lateinit var listAdapter: OreumListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        setupRecyclerView()
        // ViewModel 관찰
        observeViewModel()
        // RecyclerView 아이템 클릭 이벤트 관찰
        observeItemClicks()

        // 오름 리스트 가져오기
        viewModel.fetchOreumList()
    }

    /**
     * RecyclerView 설정
     * 어댑터를 연결하고, LayoutManager를 설정함
     */
    private fun setupRecyclerView() {
        listAdapter = OreumListAdapter { oreum -> showLottieAnimation(oreum) }
        binding?.recyclerViewOreumList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    /**
     * ViewModel의 oreumList를 관찰하고, 데이터가 업데이트되면
     * RecyclerView에 표시하도록 함
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.oreumList.collectLatest { list ->
                    listAdapter.submitList(list)
                    updateUI(list.isEmpty())
                }
            }
        }
    }

    /**
     * 데이터가 비어있을 경우와 아닐 경우에 대한 UI 처리
     * @param isEmpty 오름 리스트가 비어있는지 여부
     */
    private fun updateUI(isEmpty: Boolean) {
        binding?.apply {
            textViewEmptyOreumList.visibility = if (isEmpty) View.VISIBLE else View.GONE
            recyclerViewOreumList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    /**
     * 어댑터 내에서 발생하는 아이템 클릭 이벤트를 Flow로 관찰하여
     * 상세화면 이동 시점에 로띠 애니메이션을 표시함
     */
    private fun observeItemClicks() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listAdapter.itemClickFlow.collectLatest { oreum ->
                    showLottieAnimation(oreum)
                }
            }
        }
    }

    /**
     * 로띠 애니메이션을 재생하고, 애니메이션이 끝난 뒤 상세 화면으로 이동
     * @param oreum 선택된 오름 정보
     */
    private fun showLottieAnimation(oreum: ResultSummary) {
        binding?.lottieLoading?.apply {
            visibility = View.VISIBLE
            playAnimation()
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                    navigateToDetailFragment(oreum)
                }
            })
        }
    }

    /**
     * Navigation을 사용하여 상세화면으로 이동
     * @param oreum 선택된 오름 정보
     */
    private fun navigateToDetailFragment(oreum: ResultSummary) {
        val bundle = Bundle().apply {
            putParcelable("oreumData", oreum)
        }
        findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
    }
}
