package com.jeong.jjoreum.presentation.ui.profile.favorite

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import com.jeong.jjoreum.databinding.FragmentMyFavoriteBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.ui.list.ListAdapter
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.MyFavoriteViewModel
import com.jeong.jjoreum.repository.OreumRepositoryImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class MyFavoriteFragment :
    ViewBindingBaseFragment<FragmentMyFavoriteBinding>(FragmentMyFavoriteBinding::inflate) {

    private lateinit var viewModel: MyFavoriteViewModel
    private lateinit var listAdapter: ListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setupRecyclerView()
        observeItemClicks()
        observeFavoriteList()
    }

    private fun initViewModel() {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val apiService = RetrofitOkHttpManager.oreumRetrofitBuilder
            .create(com.jeong.jjoreum.data.model.api.OreumRetrofitInterface::class.java)
        val oreumRepo = OreumRepositoryImpl(firestore, auth, apiService)

        val factory = AppViewModelFactory(
            oreumRepository = oreumRepo
        )
        viewModel = ViewModelProvider(this, factory)[MyFavoriteViewModel::class.java]
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(
            onItemClick = { oreum -> navigateToDetailFragment(oreum) },
            onFavoriteClick = {},
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
        findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
    }
}