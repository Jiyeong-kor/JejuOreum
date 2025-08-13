package com.jeong.jjoreum.presentation.ui.profile.stamp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jeong.jjoreum.databinding.FragmentMyStampBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.MyStampViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyStampFragment :
    ViewBindingBaseFragment<FragmentMyStampBinding>(FragmentMyStampBinding::inflate) {

    private val viewModel: MyStampViewModel by viewModels()
    private lateinit var listAdapter: MyStampAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeItemClicks()

        viewModel.loadStampedList()

        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.nickname, viewModel.stampedList) { nickname, list ->
                nickname to list
            }.collectLatest { (nickname, stampedList) ->
                val count = stampedList.size

                binding?.apply {
                    myStampText.text = "${nickname}님의 스탬프"
                    myStampNum.text = "${count}개"

                    textViewEmptyStampList.visibility = if (count == 0) View.VISIBLE else View.GONE
                    recyclerViewStampList.visibility = if (count == 0) View.GONE else View.VISIBLE

                    listAdapter.submitList(stampedList)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        listAdapter = MyStampAdapter()
        binding?.recyclerViewStampList?.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = listAdapter
        }
    }

    private fun observeItemClicks() {
        viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.itemClickFlow.collectLatest { item ->
                val bundle = Bundle().apply {
                    putInt("oreumIdx", item.oreumIdx)
                    putString("oreumName", item.oreumName)
                }
                findNavController().navigate(
                    com.jeong.jjoreum.R.id.action_navigation_my_to_writeReviewFragment,
                    bundle
                )
            }
        }
    }
}
