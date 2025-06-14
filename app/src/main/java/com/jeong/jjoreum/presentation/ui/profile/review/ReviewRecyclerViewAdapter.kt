package com.jeong.jjoreum.presentation.ui.profile.review

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.databinding.RvItemDetailReviewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewRecyclerViewAdapter(
    private val onLikeClick: (ReviewItem) -> Unit,
    private val onDeleteClick: (ReviewItem) -> Unit
) : ListAdapter<ReviewItem, ReviewRecyclerViewAdapter.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ReviewItem>() {
            override fun areItemsTheSame(oldItem: ReviewItem, newItem: ReviewItem) =
                oldItem.userId == newItem.userId && oldItem.userTime == newItem.userTime

            override fun areContentsTheSame(oldItem: ReviewItem, newItem: ReviewItem) =
                oldItem == newItem
        }
    }

    inner class ViewHolder(val binding: RvItemDetailReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(item: ReviewItem) {
            with(binding) {
                reviewUserName.text = item.userNickname
                reviewContent.text = item.userReview
                val formattedTime = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                    .format(Date(item.userTime))
                reviewDate.text = formattedTime
                reviewLikeText.text = item.reviewLikeNum.toString()
                reviewProfileImage.load(R.drawable.ic_my_selected)
                reviewLikeIcon.setImageResource(
                    if (item.isLiked) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
                )
                reviewLikeLayout.setOnClickListener {
                    onLikeClick(item)
                }
                // 삭제 버튼 조건부 표시
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                reviewDeleteIcon.visibility =
                    if (item.userId == currentUserId) View.VISIBLE else View.GONE

                // 삭제 버튼 클릭 이벤트
                reviewDeleteIcon.setOnClickListener {
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("리뷰 삭제")
                        .setMessage("이 리뷰를 삭제하시겠습니까?")
                        .setPositiveButton("삭제") { _, _ ->
                            onDeleteClick(item)
                        }
                        .setNegativeButton("취소", null)
                        .show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemDetailReviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}