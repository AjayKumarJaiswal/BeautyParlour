package com.example.beautyparlour

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.beautyparlour.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // For demo, we just use a placeholder or passed color/resource
        binding.ivFullScreen.setImageResource(android.R.drawable.ic_menu_gallery)
        binding.ivFullScreen.setBackgroundColor(getColor(R.color.profile_circle))

        binding.ivClose.setOnClickListener {
            finish()
        }
    }
}