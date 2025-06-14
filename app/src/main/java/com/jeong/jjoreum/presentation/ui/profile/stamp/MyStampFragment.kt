package com.jeong.jjoreum.presentation.ui.profile.stamp

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.databinding.FragmentMyStampBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.MyStampViewModel
import com.jeong.jjoreum.repository.OreumRepositoryImpl
import com.jeong.jjoreum.repository.UserInteractionRepositoryImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MyStampFragment :
    ViewBindingBaseFragment<FragmentMyStampBinding>(FragmentMyStampBinding::inflate) {

    private lateinit var viewModel: MyStampViewModel
    private lateinit var listAdapter: MyStampAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val apiService = OreumRetrofitInterface.create()

        val oreumRepository = OreumRepositoryImpl(firestore, auth, apiService)
        val interactionRepository = UserInteractionRepositoryImpl(firestore, auth)

        val factory = AppViewModelFactory(
            oreumRepository = oreumRepository,
            interactionRepository = interactionRepository
        )

        viewModel = ViewModelProvider(this, factory)[MyStampViewModel::class.java]
    }

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
