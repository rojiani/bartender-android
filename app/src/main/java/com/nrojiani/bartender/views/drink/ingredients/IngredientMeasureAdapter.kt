package com.nrojiani.bartender.views.drink.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nrojiani.bartender.data.domain.IngredientMeasure
import com.nrojiani.bartender.databinding.IngredientMeasureItemViewBinding

class IngredientMeasureAdapter(private val clickListener: IngredientMeasureClickListener) :
    ListAdapter<IngredientMeasure, IngredientMeasureAdapter.ViewHolder>(IngredientMeasureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(private val binding: IngredientMeasureItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IngredientMeasure, clickListener: IngredientMeasureClickListener) {
            binding.ingredientMeasure = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = IngredientMeasureItemViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

// https://developer.android.com/topic/libraries/data-binding/expressions#listener_bindings
class IngredientMeasureClickListener(val clickAction: (ingredientMeasure: IngredientMeasure) -> Unit) {
    fun onClick(selectedIngredientMeasure: IngredientMeasure) = clickAction(selectedIngredientMeasure)
}

/**
 * Callback for calculating the diff between 2 list items
 */
class IngredientMeasureDiffCallback : DiffUtil.ItemCallback<IngredientMeasure>() {
    override fun areItemsTheSame(oldItem: IngredientMeasure, newItem: IngredientMeasure): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: IngredientMeasure, newItem: IngredientMeasure): Boolean =
        oldItem == newItem
}
