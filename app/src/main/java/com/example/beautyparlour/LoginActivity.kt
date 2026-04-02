package com.example.beautyparlour

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.beautyparlour.databinding.ActivityLoginBinding
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setupListeners()
    }

    private fun initViewBinding() {
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.btnGetOtp.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            if (phone.length == 10) {
                // Simulated OTP generation (Product level logic simulation)
                val generatedOtp = (100000..999999).random().toString()
                
                // Real SMS simulation: Show OTP in a Toast
                Toast.makeText(this, "Simulated SMS Sent: Your OTP is $generatedOtp", Toast.LENGTH_LONG).show()

                val intent = Intent(this, OtpActivity::class.java).apply {
                    putExtra("PHONE_NUMBER", phone)
                    putExtra("GENERATED_OTP", generatedOtp)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show()
            }
        }
    }
}