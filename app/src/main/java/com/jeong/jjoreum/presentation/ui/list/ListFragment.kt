package com.jeong.jjoreum.presentation.ui.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import com.jeong.jjoreum.databinding.FragmentListBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.ListViewModel
import com.jeong.jjoreum.repository.OreumRepositoryImpl
import com.jeong.jjoreum.repository.ReviewRepositoryImpl
import com.jeong.jjoreum.repository.StampRepositoryImpl
import com.jeong.jjoreum.repository.UserInteractionRepositoryImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListFragment : ViewBindingBaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    private lateinit var listAdapter: ListAdapter
    private lateinit var viewModel: ListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setupRecyclerView()
        observeOreumList()
        observeFragmentResult()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadOreumList()
        }
    }

    private fun initViewModel() {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val context = requireContext()

        val apiService = RetrofitOkHttpManager.oreumRetrofitBuilder
            .create(com.jeong.jjoreum.data.model.api.OreumRetrofitInterface::class.java)

        val oreumRepo = OreumRepositoryImpl(firestore, auth, apiService)
        val interactionRepo = UserInteractionRepositoryImpl(firestore, auth)
        val stampRepo = StampRepositoryImpl(context, firestore, auth)
        val reviewRepo = ReviewRepositoryImpl(firestore, auth)

        val factory = AppViewModelFactory(
            oreumRepository = oreumRepo,
            interactionRepository = interactionRepo,
            stampRepository = stampRepo,
            reviewRepository = reviewRepo
        )
        viewModel = ViewModelProvider(this, factory)[ListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(
            onItemClick = { oreum ->
                val bundle = Bundle().apply {
                    putParcelable("oreumData", oreum)
                }
                findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
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