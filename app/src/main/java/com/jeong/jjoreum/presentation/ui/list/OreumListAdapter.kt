package com.jeong.jjoreum.presentation.ui.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.ItemListBinding
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 오름 리스트를 표시하는 RecyclerView 어댑터
 * @param onItemClick 아이템 클릭 시 실행되는 콜백, 선택된 오름 데이터를 전달
 */
class OreumListAdapter(
    private val onItemClick: (ResultSummary) -> Unit
) : ListAdapter<ResultSummary, OreumListAdapter.OreumListViewHolder>(diffUtil) {

    // 아이템 클릭 이벤트를 방출하는 SharedFlow
    private val _itemClickFlow = MutableSharedFlow<ResultSummary>(extraBufferCapacity = 1)
    val itemClickFlow = _itemClickFlow.asSharedFlow()

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ResultSummary>() {
            override fun areItemsTheSame(oldItem: ResultSummary, newItem: ResultSummary): Boolean =
                oldItem.idx == newItem.idx

            override fun areContentsTheSame(oldItem: ResultSummary, newItem: ResultSummary): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OreumListViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OreumListViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: OreumListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder 클래스
     * @param binding 아이템 레이아웃 뷰 바인딩
     * @param onItemClick 아이템 클릭 시 호출되는 함수
     */
    class OreumListViewHolder(
        private val binding: ItemListBinding,
        private val onItemClick: (ResultSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * 아이템 뷰에 데이터를 바인딩하는 함수
         * @param item 리스트에서 가져온 ResultSummary
         */
        @SuppressLint("SetTextI18n")
        fun bind(item: ResultSummary) = with(binding) {
            val imageLoader = ImageLoader.Builder(root.context)
                .okHttpClient { RetrofitOkHttpManager.getUnsafeOkHttpClient() }
                .build()

            val request = ImageRequest.Builder(root.context)
                .data(item.imgPath)
                .crossfade(true)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .target(rvItemImage)
                .build()

            imageLoader.enqueue(request)

            rvItemOreumName.text = item.oreumKname
            stampText.text = item.stamp.toString()
            favoriteCountText.text = item.favorite.toString()

            // 즐겨찾기 여부에 따라 아이콘 설정
            favoriteIcon.setImageResource(
                if (item.isFavorite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_variant
            )

            // 스탬프 획득 여부에 따라 아이콘 설정
            stampIcon.setImageResource(
                if (item.hasStamp) R.drawable.ic_stamp_selected else R.drawable.ic_stamp_variant
            )

            // 아이템 전체 클릭 시
            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
