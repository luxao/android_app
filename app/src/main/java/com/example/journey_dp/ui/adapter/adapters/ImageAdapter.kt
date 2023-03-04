package com.example.journey_dp.ui.adapter.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.journey_dp.R
import com.example.journey_dp.data.domain.Step
import com.example.journey_dp.databinding.ImageItemLayoutBinding
import com.example.journey_dp.databinding.StepItemBinding
import com.example.journey_dp.utils.clearFromHtml

class ImageAdapter : ListAdapter<Bitmap, ImageAdapter.ImageItemViewHolder>(DiffCallback) {

    class ImageItemViewHolder(var binding: ImageItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bitmap: Bitmap) {
            binding.itemImage.setImageBitmap(bitmap)
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Bitmap>() {
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem.generationId == newItem.generationId
        }

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem.sameAs(newItem)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        return ImageItemViewHolder(
            ImageItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val bitmap: Bitmap = getItem(position)
        holder.bind(bitmap)
    }
}