package com.example.beautyparlour

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

        // WhatsApp-style keyboard handling logic
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            
            // Set top padding for status bar and bottom padding for keyboard/nav bar
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = if (imeInsets.bottom > 0) imeInsets.bottom else systemBars.bottom
            )
            
            // Automatically scroll to bottom when keyboard opens
            if (imeInsets.bottom > 0 && messages.isNotEmpty()) {
                binding.rvMessages.postDelayed({
                    binding.rvMessages.smoothScrollToPosition(messages.size - 1)
                }, 100)
            }
            
            insets
        }
    }

    private fun setupChat() {
        adapter = ChatAdapter(messages)
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Start showing messages from bottom
        }
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