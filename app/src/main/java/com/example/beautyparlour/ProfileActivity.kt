package com.example.beautyparlour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.beautyparlour.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val sharedPrefs by lazy { getSharedPreferences("UserPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        refreshUI()
        setupListeners()
    }

    private fun initViewBinding() {
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun refreshUI() {
        with(binding) {
            val name = sharedPrefs.getString("user_name", "Ajay Kumar")
            val bio = sharedPrefs.getString("user_bio", "Beauty Enthusiast")
            val desc = sharedPrefs.getString("user_desc", "Style is a way to say who you are...")

            tvCurrentName.text = name
            tvValName.text = name
            tvValBio.text = bio
            tvValDesc.text = desc
        }
    }

    private fun setupListeners() {
        with(binding) {
            ivBack.setOnClickListener { finish() }

            btnEditName.setOnClickListener {
                showEditDialog("Edit Name", tvValName.text.toString()) { newValue ->
                    sharedPrefs.edit().putString("user_name", newValue).apply()
                    refreshUI()
                    showToast("Name saved")
                }
            }

            btnEditBio.setOnClickListener {
                showEditDialog("Edit Bio", tvValBio.text.toString()) { newValue ->
                    sharedPrefs.edit().putString("user_bio", newValue).apply()
                    refreshUI()
                    showToast("Bio saved")
                }
            }

            btnEditDesc.setOnClickListener {
                showEditDialog("Edit Description", sharedPrefs.getString("user_desc", "") ?: "") { newValue ->
                    sharedPrefs.edit().putString("user_desc", newValue).apply()
                    refreshUI()
                    showToast("Description saved")
                }
            }

            btnEditProfilePic.setOnClickListener {
                showToast("Profile picture feature coming soon!")
            }

            btnLogout.setOnClickListener {
                showLogoutConfirmation()
            }
        }
    }

    private fun showEditDialog(title: String, currentValue: String, onUpdate: (String) -> Unit) {
        val container = FrameLayout(this)
        val input = EditText(this)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(64, 32, 64, 32)
        }
        input.layoutParams = params
        input.setText(currentValue)
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    onUpdate(text)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Logout") { _, _ ->
                sharedPrefs.edit().clear().apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}