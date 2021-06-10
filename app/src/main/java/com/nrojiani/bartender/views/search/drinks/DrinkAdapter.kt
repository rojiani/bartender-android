package com.nrojiani.bartender.views.search.drinks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nrojiani.bartender.data.domain.Drink
import com.nrojiani.bartender.databinding.DrinkItemViewBinding

class DrinkAdapter(private val clickListener: DrinkClickListener) :
    ListAdapter<Drink, DrinkAdapter.ViewHolder>(DrinkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(private val binding: DrinkItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Drink, clickListener: DrinkClickListener) {
            binding.drink = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DrinkItemViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

// https://developer.android.com/topic/libraries/data-binding/expressions#listener_bindings
class DrinkClickListener(val clickAction: (drink: Drink) -> Unit) {
    fun onClick(selectedDrink: Drink) = clickAction(selectedDrink)
}

/**
 * Callback for calculating the diff between 2 list items
 */
class DrinkDiffCallback : DiffUtil.ItemCallback<Drink>() {
    override fun areItemsTheSame(oldItem: Drink, newItem: Drink): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: Drink, newItem: Drink): Boolean =
        oldItem == newItem
}
