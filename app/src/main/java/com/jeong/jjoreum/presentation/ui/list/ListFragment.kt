package com.jeong.jjoreum.presentation.ui.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.FragmentListBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.ListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment :
    ViewBindingBaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    private lateinit var listAdapter: ListAdapter
    private val viewModel: ListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeOreumList()
        observeFragmentResult()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadOreumList()
        }
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(
            onItemClick = { oreum ->
                val bundle = Bundle().apply {
                    putParcelable("oreumData", oreum)
                }
                findNavController().navigate(
                    R.id.action_listFragment_to_detailFragment, bundle
                )
            },
            onFavoriteClick = { oreum ->
                viewModel.toggleFavorite(oreum.idx.toString())
            },
            onStampClick = { oreum ->
                viewModel.tryStamp(
                    oreumIdx = oreum.idx.toString(),
                    oreumName = oreum.oreumKname,
                    oreumLat = oreum.y,
                    oreumLng = oreum.x,
                    onSuccess = { showToast("스탬프 완료!") },
                    onFailure = { msg -> showToast(msg) }
                )
            }

        )

        binding?.recyclerViewOreumList?.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeOreumList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.oreumList.collectLatest { list ->
                listAdapter.submitList(list)
            }
        }
    }

    private fun observeFragmentResult() {
        parentFragmentManager.setFragmentResultListener(
            "oreum_update",
            viewLifecycleOwner
        ) { _, bundle ->
            val shouldRefresh = bundle.getBoolean("shouldRefresh", false)
            val updatedOreumIdx = bundle.getInt("oreumIdx", -1)

            if (shouldRefresh && updatedOreumIdx != -1) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.refreshOreumById(updatedOreumIdx.toString())
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}