package com.jeong.jjoreum.presentation.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PermissionManager
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import com.jeong.jjoreum.databinding.FragmentDetailBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.ui.profile.review.ReviewRecyclerViewAdapter
import com.jeong.jjoreum.presentation.viewmodel.AppViewModelFactory
import com.jeong.jjoreum.presentation.viewmodel.DetailViewModel
import com.jeong.jjoreum.repository.ReviewRepositoryImpl
import com.jeong.jjoreum.repository.StampRepositoryImpl
import com.jeong.jjoreum.repository.UserInteractionRepositoryImpl
import com.jeong.jjoreum.util.toastMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailFragment :
    ViewBindingBaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private lateinit var viewModel: DetailViewModel
    private lateinit var reviewAdapter: ReviewRecyclerViewAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            PermissionManager.setLocationGranted(requireContext(), true)
            stampWithLocationCheck()
        } else {
            PermissionManager.setLocationGranted(requireContext(), false)
            toastMessage("위치 권한이 필요합니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = requireContext()
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val factory = AppViewModelFactory(
            interactionRepository = UserInteractionRepositoryImpl(firestore, auth),
            reviewRepository = ReviewRepositoryImpl(firestore, auth),
            stampRepository = StampRepositoryImpl(context, firestore, auth)
        )
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val oreumData = DetailFragmentArgs.fromBundle(requireArguments()).oreumData
        with(viewModel) {
            setOreumDetail(oreumData)
            loadReviews(oreumData.idx.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reviewAdapter = ReviewRecyclerViewAdapter(onLikeClick = {}, onDeleteClick = {})
        binding?.detailReviewRv?.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = reviewAdapter
        }

        binding?.detailFavorite?.setOnClickListener {
            val oreumIdx = viewModel.oreumDetail.value?.idx?.toString() ?: return@setOnClickListener
            val isCurrentlyLiked = viewModel.isFavorite.value
            viewModel.toggleFavorite(oreumIdx)
            toastMessage(if (!isCurrentlyLiked) "관심 오름에 추가되었습니다" else "관심 오름에서 제외되었습니다")
            setFragmentResult("oreum_update", Bundle().apply {
                putBoolean("shouldRefresh", true)
                putInt("oreumIdx", oreumIdx.toInt())
            })
        }

        binding?.detailStamp?.setOnClickListener {
            val oreum = viewModel.oreumDetail.value ?: return@setOnClickListener
            if (viewModel.hasStamp.value) {
                val action = DetailFragmentDirections.actionDetailFragmentToWriteReviewFragment(
                    oreum.idx, oreum.oreumKname
                )
                findNavController().navigate(action)
            } else {
                if (PermissionManager.isLocationGranted(requireContext()) ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {

                    stampWithLocationCheck()
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

            }
        }

        observeViewModel()
    }

    private fun stampWithLocationCheck() {
        val oreum = viewModel.oreumDetail.value ?: return
        viewModel.stampOreum(
            oreumIdx = oreum.idx.toString(),
            oreumName = oreum.oreumKname,
            lat = oreum.y,
            lng = oreum.x
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.oreumDetail.collectLatest { oreum ->
                oreum?.let {
                    binding?.apply {
                        detailName.text = it.oreumKname
                        detailAddr.text = it.oreumAddr
                        detailExplain.text = it.explain

                        val imageLoader = ImageLoader.Builder(requireContext())
                            .okHttpClient { RetrofitOkHttpManager.getUnsafeOkHttpClient() }
                            .build()

                        val request = ImageRequest.Builder(requireContext())
                            .data(if (it.imgPath.startsWith("http")) it.imgPath else "https://example.com/default_image.jpg")
                            .crossfade(true)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .target(detailImage)
                            .build()

                        imageLoader.enqueue(request)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collectLatest {
                binding?.detailFavorite?.isChecked = it
            }
        }

        lifecycleScope.launch {
            viewModel.hasStamp.collectLatest {
                binding?.detailStamp?.text = if (it) "후기 작성" else "스탬프 찍기"
            }
        }

        lifecycleScope.launch {
            viewModel.reviewList.collectLatest { reviews ->
                reviewAdapter.submitList(reviews)
                binding?.detailReviewEmpty?.visibility =
                    if (reviews.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.event.collectLatest { event ->
                when (event) {
                    is DetailViewModel.DetailEvent.StampSuccess -> {
                        toastMessage("스탬프가 인증되었습니다!")
                        setFragmentResult("oreum_update", Bundle().apply {
                            putBoolean("shouldRefresh", true)
                        })
                    }

                    is DetailViewModel.DetailEvent.StampFailure -> {
                        toastMessage(event.message)
                    }

                    null -> {}
                }
                viewModel.clearEvent()
            }
        }
    }
}