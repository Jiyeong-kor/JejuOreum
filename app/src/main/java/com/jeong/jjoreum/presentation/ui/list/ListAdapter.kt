package com.jeong.jjoreum.presentation.ui.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.databinding.ItemListBinding
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ListAdapter(
    private val onItemClick: (ResultSummary) -> Unit,
    private val onFavoriteClick: (ResultSummary) -> Unit,
    private val onStampClick: (ResultSummary) -> Unit
) : ListAdapter<ResultSummary, com.jeong.jjoreum.presentation.ui.list.ListAdapter.OreumListViewHolder>(
    diffUtil
) {

    private val _itemClickFlow = MutableSharedFlow<ResultSummary>(extraBufferCapacity = 1)
    val itemClickFlow = _itemClickFlow.asSharedFlow()

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ResultSummary>() {
            override fun areItemsTheSame(oldItem: ResultSummary, newItem: ResultSummary): Boolean =
                oldItem.idx == newItem.idx

            override fun areContentsTheSame(
                oldItem: ResultSummary,
                newItem: ResultSummary
            ): Boolean {
                return oldItem.totalFavorites == newItem.totalFavorites &&
                        oldItem.userLiked == newItem.userLiked &&
                        oldItem.userStamped == newItem.userStamped &&
                        oldItem.oreumKname == newItem.oreumKname &&
                        oldItem.imgPath == newItem.imgPath
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OreumListViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OreumListViewHolder(binding, onItemClick, onFavoriteClick, onStampClick)
    }

    override fun onBindViewHolder(holder: OreumListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OreumListViewHolder(
        private val binding: ItemListBinding,
        private val onItemClick: (ResultSummary) -> Unit,
        private val onFavoriteClick: (ResultSummary) -> Unit,
        private val onStampClick: (ResultSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bind(item: ResultSummary) = with(binding) {
            rvItemImage.load(item.imgPath) {
                crossfade(true)
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
            }

            rvItemOreumName.text = item.oreumKname
            stampText.text = item.totalStamps.toString()
            favoriteCountText.text = item.totalFavorites.toString()

            favoriteIcon.setOnClickListener {
                onFavoriteClick(item)
            }

            stampIcon.setOnClickListener {
                onStampClick(item)
            }

            favoriteIcon.setImageResource(
                if (item.userLiked) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
            )

            stampIcon.setImageResource(
                if (item.userStamped) R.drawable.ic_stamp_selected else R.drawable.ic_stamp_variant
            )

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
