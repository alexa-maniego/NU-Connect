package com.example.nuconnect

import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.preference.PreferenceManager

class Welcomepage : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var wifiStateReceiver: WifiStateReceiver
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcomepage)

        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userEmail = getUserEmail()
        if (userEmail != null) {
            val user = dbHelper.getUserByEmail(userEmail)
            if (user != null) {
                val welcomeTextView = findViewById<TextView>(R.id.textViewWelcome)
                welcomeTextView.text = "Welcome, ${user.firstName}!"
            }
        }

        val imageProfileButton = findViewById<ImageButton>(R.id.imageProfileButton)
        imageProfileButton.setOnClickListener {
            startActivity(Intent(this, Pofile::class.java))
        }

        val seeMoreButton = findViewById<Button>(R.id.SeeMore)
        seeMoreButton.setOnClickListener {
            startActivity(Intent(this, TimeRemaining::class.java))
        }

        val generateCodeButton = findViewById<Button>(R.id.GenerateCode)
        generateCodeButton.setOnClickListener {
            startActivity(Intent(this, GenerateCode::class.java))
        }

        val faqsButton = findViewById<Button>(R.id.FAQs)
        faqsButton.setOnClickListener {
            startActivity(Intent(this, faqs::class.java))
        }

        val imageSettingsButton = findViewById<ImageButton>(R.id.imageSettingsButton)
        imageSettingsButton.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        // Initialize and register the BroadcastReceiver
        wifiStateReceiver = WifiStateReceiver(findViewById(R.id.status), this)

        val intentFilter = IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)  // General connectivity change
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)    // Wi-Fi on/off state
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION) // Wi-Fi network changes
        }
        registerReceiver(wifiStateReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        updateConnectionStatus()
        handler.post(checkConnectionRunnable) // Start periodic check
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(checkConnectionRunnable) // Stop checking when paused
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiStateReceiver) // Unregister to prevent memory leaks
    }

    private val checkConnectionRunnable = object : Runnable {
        override fun run() {
            updateConnectionStatus()
            handler.postDelayed(this, 5000) // Check every 5 seconds
        }
    }

    private fun updateConnectionStatus() {
        val statusTextView = findViewById<TextView>(R.id.status)

        // Initialize SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Get the user email
        val email = getUserEmail() ?: ""

        // Get the connection status from SharedPreferences
        val isConnected = sharedPreferences.getBoolean("isConnected_${email}", false)

        // Update the status TextView based on connection status
        if (isConnected) {
            statusTextView.text = "Connected"
            statusTextView.setTextColor(resources.getColor(android.R.color.holo_green_dark)) // Green color
        } else {
            statusTextView.text = "Not Connected"
            statusTextView.setTextColor(resources.getColor(android.R.color.holo_red_dark))   // Red color
        }
    }

    private fun getUserEmail(): String? {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userEmail", null)
    }
}
