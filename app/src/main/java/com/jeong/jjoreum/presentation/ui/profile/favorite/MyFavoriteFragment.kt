package com.jeong.jjoreum.presentation.ui.profile.favorite

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.databinding.FragmentMyFavoriteBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.ui.list.ListAdapter
import com.jeong.jjoreum.presentation.viewmodel.MyFavoriteViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFavoriteFragment :
    ViewBindingBaseFragment<FragmentMyFavoriteBinding>(FragmentMyFavoriteBinding::inflate) {

    private val viewModel: MyFavoriteViewModel by viewModels()
    private lateinit var listAdapter: ListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeItemClicks()
        observeFavoriteList()
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(
            onItemClick = { oreum -> navigateToDetailFragment(oreum) },
            onFavoriteClick = { oreum ->
                viewModel.toggleFavorite(
                    oreum.idx.toString(), !oreum.userLiked
                )
            },
            onStampClick = {}
        )

        binding?.recyclerViewFavoriteList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun observeFavoriteList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteList.collectLatest { favoriteOreums ->
                val isEmpty = favoriteOreums.isEmpty()
                binding?.apply {
                    textViewEmptyFavoriteList.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    recyclerViewFavoriteList.visibility = if (isEmpty) View.GONE else View.VISIBLE
                }
                listAdapter.submitList(favoriteOreums)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeItemClicks() {
        viewLifecycleOwner.lifecycleScope.launch {
            listAdapter.itemClickFlow
                .debounce(500)
                .collect { navigateToDetailFragment(it) }
        }
    }

    private fun navigateToDetailFragment(oreum: ResultSummary) {
        val bundle = Bundle().apply {
            putParcelable("oreumData", oreum)
        }
        findNavController().navigate(R.id.detailFragment, bundle)
    }
}