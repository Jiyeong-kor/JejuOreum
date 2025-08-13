package com.jeong.jjoreum.presentation.ui.profile.review

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.databinding.FragmentWriteReviewBinding
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.WriteReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WriteReviewFragment :
    ViewBindingBaseFragment<FragmentWriteReviewBinding>(
        FragmentWriteReviewBinding::inflate
    ) {

    private val viewModel: WriteReviewViewModel by viewModels()
    private lateinit var reviewAdapter: ReviewRecyclerViewAdapter

    private var oreumIdx: Int = -1
    private var oreumName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            oreumIdx = it.getInt("oreumIdx", -1)
            oreumName = it.getString("oreumName") ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.textOreumName?.text = oreumName

        reviewAdapter = ReviewRecyclerViewAdapter(
            onLikeClick = { reviewItem ->
                viewModel.toggleReviewLike(oreumIdx.toString(), reviewItem.userId)
            },
            onDeleteClick = { reviewItem ->
                viewModel.deleteReview(
                    oreumIdx.toString(),
                    reviewItem.userId
                ) { success ->
                    val msg = if (success) "리뷰 삭제 완료!" else "리뷰 삭제 실패"
                    Toast.makeText(
                        requireContext(), msg, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        binding?.recyclerReviewList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter
        }

        viewModel.loadReviews(oreumIdx.toString())
        observeReviews()

        binding?.btnSaveReview?.setOnClickListener {
            val reviewText = binding?.editReview?.text?.toString()?.trim()
            if (reviewText.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(), "후기를 입력하세요.", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance()
                    .collection("user_info_col")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val nickname = snapshot.getString("nickname") ?: "익명"
                        val reviewItem = ReviewItem(
                            userId = uid,
                            userNickname = nickname,
                            userReview = reviewText,
                            userTime = System.currentTimeMillis(),
                            reviewLikeNum = 0,
                            isLiked = false
                        )
                        viewModel.saveReview(
                            oreumIdx = oreumIdx.toString(),
                            review = reviewItem
                        ) { success ->
                            if (success) {
                                Toast.makeText(
                                    requireContext(),
                                    "후기 저장 완료!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding?.editReview?.setText("")
                                // 저장 후 목록 갱신
                                viewModel.loadReviews(oreumIdx.toString())
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "후기 저장 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
            }
        }
    }

    private fun observeReviews() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reviews.collectLatest { reviews ->
                reviewAdapter.submitList(reviews)
                binding?.textEmptyReview?.visibility =
                    if (reviews.isEmpty()) View.VISIBLE else View.GONE
                binding?.recyclerReviewList?.visibility =
                    if (reviews.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }
}