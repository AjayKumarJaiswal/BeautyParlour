package com.example.beautyparlour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LEFT = 0
        private const val TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isMine) TYPE_RIGHT else TYPE_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LEFT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_left, parent, false)
            LeftViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_right, parent, false)
            RightViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = messages[position]
        if (holder is LeftViewHolder) {
            holder.tvMessage.text = chatMessage.message
            holder.tvTime.text = chatMessage.time
        } else if (holder is RightViewHolder) {
            holder.tvMessage.text = chatMessage.message
            holder.tvTime.text = chatMessage.time
        }
    }

    override fun getItemCount(): Int = messages.size

    class LeftViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    class RightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }
}