package com.example.beautyparlour

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beautyparlour.databinding.ActivityChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setupChat()
        setupListeners()
        
        // Initial bot message
        addBotMessage(getString(R.string.chat_initial_response))
    }

    private fun initViewBinding() {
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupChat() {
        adapter = ChatAdapter(messages)
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                addUserMessage(text)
                binding.etMessage.setText("")
                simulateResponse(text)
            }
        }
    }

    private fun addUserMessage(text: String) {
        messages.add(ChatMessage(text, getCurrentTime(), true))
        adapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun addBotMessage(text: String) {
        messages.add(ChatMessage(text, getCurrentTime(), false))
        adapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun simulateResponse(userMsg: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val response = when {
                userMsg.contains("price", true) || userMsg.contains("cost", true) -> 
                    "The treatment costs depend on the service. For example, Keratin is ₹2999."
                userMsg.contains("appointment", true) || userMsg.contains("book", true) -> 
                    "You can book an appointment directly through our app or I can help you here!"
                else -> "Of course! How can I help you with our services today?"
            }
            addBotMessage(response)
        }, 1500)
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }
}