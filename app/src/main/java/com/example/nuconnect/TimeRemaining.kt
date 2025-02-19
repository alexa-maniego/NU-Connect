package com.example.nuconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast

class TimeRemaining : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var totalTimeUsedTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var userEmail: String
    private var sessionDuration: Long = 0L  // Total session time in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_time_remaining)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar)
        timerTextView = findViewById(R.id.timerTextView)
        totalTimeUsedTextView = findViewById(R.id.totalTimeUsedTextView)

        // Set up navigation buttons
        findViewById<ImageButton>(R.id.imageHomeButton).setOnClickListener {
            startActivity(Intent(this, Welcomepage::class.java))
        }

        findViewById<ImageButton>(R.id.imageProfileButton).setOnClickListener {
            startActivity(Intent(this, Pofile::class.java))
        }

        findViewById<ImageButton>(R.id.imageSettingsButton).setOnClickListener {
            startActivity(Intent(this, Welcomepage::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton6).setOnClickListener {
            startActivity(Intent(this, Welcomepage::class.java))
        }

        // Get user email from SharedPreferences
        userEmail = getUserEmail() ?: ""

        // Initialize SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Retrieve start and end times for the session
        val startTime = sharedPreferences.getLong("startTime_$userEmail", 0L)
        val endTime = sharedPreferences.getLong("endTime_$userEmail", 0L)

        if (endTime > System.currentTimeMillis()) {
            val remainingTime = endTime - System.currentTimeMillis()
            sessionDuration = endTime - startTime  // Store the total session time
            progressBar.max = 100  // Ensure max value is set
            startTimer(remainingTime)
            displayTotalTimeUsed(startTime, System.currentTimeMillis())
        } else {
            // Timer has expired or not set
            timerTextView.text = "00:00:00"
            totalTimeUsedTextView.text = "00:00:00"
            progressBar.progress = 0
            Toast.makeText(this, "Your session has expired!", Toast.LENGTH_LONG).show()
        }
    }

    private fun startTimer(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                // Update progress bar
                val progress = (100 - ((millisUntilFinished.toFloat() / sessionDuration.toFloat()) * 100)).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                timerTextView.text = "00:00:00"
                progressBar.progress = 0
                Toast.makeText(this@TimeRemaining, "Session time is over!", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun displayTotalTimeUsed(startTime: Long, currentTime: Long) {
        val totalTimeUsed = currentTime - startTime
        val hours = totalTimeUsed / (1000 * 60 * 60)
        val minutes = (totalTimeUsed / (1000 * 60)) % 60
        val seconds = (totalTimeUsed / 1000) % 60
        totalTimeUsedTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun getUserEmail(): String? {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userEmail", null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel() // Stops the timer when the activity is destroyed
        }
    }

}
