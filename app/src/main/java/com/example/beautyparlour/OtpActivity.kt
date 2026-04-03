package com.example.beautyparlour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.beautyparlour.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private var phoneNumber: String? = null
    private var correctOtp: String? = null
    private val sharedPrefs by lazy { getSharedPreferences("UserPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        
        phoneNumber = intent.getStringExtra("PHONE_NUMBER")
        correctOtp = intent.getStringExtra("GENERATED_OTP")
        
        binding.tvSubtitle.text = "OTP sent to +91 $phoneNumber"

        setupListeners()
    }

    private fun initViewBinding() {
        enableEdgeToEdge()
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnVerify.setOnClickListener {
            val enteredOtp = binding.etOtp.text.toString().trim()
            
            // Validate entered OTP against the simulated generated one
            if (enteredOtp == correctOtp) {
                // Set login status to true
                sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvResend.setOnClickListener {
            // Regenerate simulated OTP
            correctOtp = (100000..999999).random().toString()
            Toast.makeText(this, "Simulated SMS Resent: Your OTP is $correctOtp", Toast.LENGTH_LONG).show()
        }
    }
}