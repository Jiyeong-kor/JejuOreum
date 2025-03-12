package com.jeong.jjoreum.presentation.ui.review

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.RvItemDetailReviewBinding
import com.jeong.jjoreum.data.model.entity.ReviewItem

/**
 * 상세 화면(Detail)에서 리뷰 목록을 표시하는 RecyclerView 어댑터
 * @param itemList 리뷰 목록
 * @param onLikeClick 좋아요(Like) 버튼 클릭 시 처리할 동작
 */
class DetailReviewRVAdapter(
    private val itemList: MutableList<ReviewItem>,
    private val onLikeClick: (ReviewItem) -> Unit
) : RecyclerView.Adapter<DetailReviewRVAdapter.ViewHolder>() {

    /**
     * 뷰 홀더 클래스
     * @param binding 뷰 바인딩 객체
     */
    inner class ViewHolder(val binding: RvItemDetailReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ReviewItem) {
            with(binding) {
                // 사용자 닉네임
                reviewUserName.text = item.userNickname
                // 리뷰 내용
                reviewContent.text = item.userReview
                // 리뷰 작성 시간
                reviewDate.text = item.userTime.toString()
                // 좋아요 수
                reviewLikeText.text = item.reviewLikeNum.toString()

                // 프로필 이미지 로드 (임시로 R.drawable.ic_my_selected 사용)
                reviewProfileImage.load(R.drawable.ic_my_selected)

                // 좋아요 여부에 따라 아이콘 변경
                reviewLikeIcon.setImageResource(
                    if (item.isLiked) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
                )
                // 좋아요 레이아웃 클릭 시 이벤트 처리
                reviewLikeLayout.setOnClickListener {
                    onLikeClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RvItemDetailReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}
