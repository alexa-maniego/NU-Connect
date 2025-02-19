package com.example.nuconnect

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.content.ContextCompat

class Settings : AppCompatActivity() {
    private lateinit var notifbut: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        notifbut = findViewById(R.id.notifbut)

        // Load saved state
        notifbut.isChecked = sharedPreferences.getBoolean("notifications_enabled", false)

        notifbut.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPreferences.edit()) {
                putBoolean("notifications_enabled", isChecked)
                apply()
            }

            val thumbColor = if (isChecked) android.R.color.holo_green_light else android.R.color.darker_gray
            val trackColor = if (isChecked) android.R.color.holo_green_dark else android.R.color.black

            notifbut.thumbDrawable.setTint(ContextCompat.getColor(this, thumbColor))
            notifbut.trackDrawable.setTint(ContextCompat.getColor(this, trackColor))

            val message = if (isChecked) "Notification On" else "Notification Off"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }


        val imageButtonAccount = findViewById<ImageButton>(R.id.imageButtonAccount)
        imageButtonAccount.setOnClickListener {
            val intent = Intent(this, Pofile::class.java)
            startActivity(intent)
        }

        val imageButtonFAQs = findViewById<ImageButton>(R.id.imageButtonfaqsn)
        imageButtonFAQs.setOnClickListener {
            val intent = Intent(this, faqs::class.java)
            startActivity(intent)
        }

        val imageButton4 = findViewById<ImageButton>(R.id.imageButton4)
        imageButton4.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }
        val imageProfileButton = findViewById<ImageButton>(R.id.imageProfileButton)
        imageProfileButton.setOnClickListener {
            val intent = Intent(this, Pofile::class.java)
            startActivity(intent)
        }

        val imageButtonAbout = findViewById<ImageButton>(R.id.imageButtonAbout)
        imageButtonAbout.setOnClickListener {
            val intent = Intent(this, about::class.java)
            startActivity(intent)
        }

        val imageButtonLogout = findViewById<ImageButton>(R.id.imageButtonLogout)
        imageButtonLogout.setOnClickListener {
            clearLoginState()
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
            finish()
        }

        val imageButtonBack = findViewById<ImageButton>(R.id.imageButton12)
        imageButtonBack.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }
    }

    private fun clearLoginState() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
    }
}
