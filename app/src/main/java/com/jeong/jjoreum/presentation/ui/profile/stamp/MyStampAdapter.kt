package com.jeong.jjoreum.presentation.ui.profile.stamp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jeong.jjoreum.databinding.ItemStampBinding
import com.jeong.jjoreum.data.model.entity.MyStampItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 나의 스탬프 목록을 표시하는 RecyclerView 어댑터
 */
class MyStampAdapter : ListAdapter<MyStampItem, MyStampAdapter.MyStampViewHolder>(diffUtil) {

    // 아이템 클릭 이벤트를 방출하기 위한 Flow
    private val _itemClickFlow = MutableSharedFlow<MyStampItem>(extraBufferCapacity = 1)
    val itemClickFlow = _itemClickFlow.asSharedFlow()

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MyStampItem>() {
            override fun areItemsTheSame(oldItem: MyStampItem, newItem: MyStampItem): Boolean =
                oldItem.oreumIdx == newItem.oreumIdx

            override fun areContentsTheSame(oldItem: MyStampItem, newItem: MyStampItem): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStampViewHolder {
        val binding = ItemStampBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyStampViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyStampViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * 스탬프 아이템을 표시하는 ViewHolder
     */
    inner class MyStampViewHolder(private val binding: ItemStampBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyStampItem) {
            // item 뷰를 클릭 시 Flow로 이벤트 방출
            binding.root.setOnClickListener {
                _itemClickFlow.tryEmit(item)
            }
        }
    }
}
