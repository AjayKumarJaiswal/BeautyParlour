package com.example.beautyparlour

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beautyparlour.databinding.ActivityBookingBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupUI()
        loadBookingData()
    }

    private fun setupUI() {
        binding.ivBackBooking.setOnClickListener {
            finish()
        }

        binding.btnConfirmBooking.setOnClickListener {
            Toast.makeText(this, "Booking Confirmed! We will contact you soon.", Toast.LENGTH_LONG).show()
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadBookingData() {
        val itemsJson = intent.getStringExtra("booking_items")
        if (itemsJson != null) {
            val itemType = object : TypeToken<List<ServiceItem>>() {}.type
            val items: List<ServiceItem> = Gson().fromJson(itemsJson, itemType)

            binding.rvBookingItems.layoutManager = LinearLayoutManager(this)
            binding.rvBookingItems.adapter = CartAdapter(items, showRemove = false)

            val total = items.sumOf { it.price.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            binding.tvBookingTotal.text = "₹$total"
        }
    }
}
