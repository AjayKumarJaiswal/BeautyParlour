package com.example.beautyparlour

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.beautyparlour.databinding.ItemCartBinding

class CartAdapter(
    private val items: List<ServiceItem>,
    private val showRemove: Boolean = true,
    private val onRemoveClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ServiceItem, position: Int) {
            binding.tvCartItemTitle.text = item.title
            binding.tvCartItemPrice.text = item.price
            binding.btnRemove.isVisible = showRemove
            binding.btnRemove.setOnClickListener { onRemoveClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
