package com.jeong.jjoreum.presentation.ui.map

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onItemClick: (ResultSummary) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    var searchResults: List<ResultSummary> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val item = searchResults[position]
        with(holder.binding) {
            textViewTitle.text = item.oreumKname
            textViewSubtitle.text = item.oreumAddr

            imageViewThumbnail.load(item.imgPath) {
                crossfade(true)
                allowHardware(false)
                memoryCacheKey(item.imgPath)
                diskCacheKey(item.imgPath)
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
                listener(
                    onSuccess = { _, _ -> Log.d("CoilLoad", "이미지 로딩 성공: ${item.imgPath}") },
                    onError = { request: ImageRequest, result: ErrorResult ->
                        Log.e("CoilLoadError", "이미지 로드 실패: ${item.imgPath}", result.throwable)
                    }
                )
            }

            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = searchResults.size

    class SearchResultViewHolder(val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root)
}