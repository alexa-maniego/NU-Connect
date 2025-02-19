package com.example.nuconnect

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.SharedPreferences
import android.preference.PreferenceManager
import kotlin.random.Random

class GenerateCode : AppCompatActivity() {
    private lateinit var clickOnConnect: TextView
    private lateinit var buttonConnect: Button
    private lateinit var backButton: ImageButton
    private lateinit var buttonGenerateForAnotherDevice: Button
    private lateinit var buttonGenerateCode: Button
    private lateinit var codeTextViews: Array<TextView>
    private lateinit var userEmail: String
    private val handler = Handler(Looper.getMainLooper())
    private val disconnectRunnable = Runnable { disconnectWiFi() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_generate_code)

        clickOnConnect = findViewById(R.id.ClickonConnect)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userEmail = getUserEmail() ?: ""

        val text = "Click on connect to automatically connect to the wifi"
        val spannableString = SpannableString(text)
        val start = text.indexOf("connect")
        val end = start + "connect".length

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        clickOnConnect.text = spannableString

        buttonConnect = findViewById(R.id.buttonConnect)
        backButton = findViewById(R.id.imageButtonback1)
        buttonGenerateForAnotherDevice = findViewById(R.id.buttonGenerateforanotherdevice)
        buttonGenerateCode = findViewById(R.id.buttonGenerateCode)

        codeTextViews = arrayOf(
            findViewById(R.id.code1),
            findViewById(R.id.code2),
            findViewById(R.id.code3),
            findViewById(R.id.code4),
            findViewById(R.id.code5),
            findViewById(R.id.code6),
            findViewById(R.id.code7),
            findViewById(R.id.code8)
        )

        displayAsterisks()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val endTime = sharedPreferences.getLong("endTime_$userEmail", 0L)
        val currentTime = System.currentTimeMillis()
        if (endTime > currentTime) {
            buttonConnect.isEnabled = false
            buttonConnect.text = "Already Connected"
            buttonGenerateCode.isEnabled = false
            buttonGenerateCode.text = "Code Generation Disabled"

            val savedCode = sharedPreferences.getString("savedCode_$userEmail", "********")
            for (i in savedCode!!.indices) {
                codeTextViews[i].text = savedCode[i].toString()
            }

            val remainingTime = endTime - currentTime
            handler.postDelayed(disconnectRunnable, remainingTime)
        } else {
            buttonConnect.isEnabled = false
            buttonGenerateCode.isEnabled = true
            buttonGenerateCode.setOnClickListener {
                generateAndDisplayCode()
                buttonConnect.isEnabled = true
            }
        }

        buttonConnect.setOnClickListener {
            val savedCode = sharedPreferences.getString("savedCode_$userEmail", null)
            if (savedCode != null) {
                WifiConnector.connectToWiFi(this, "NU Wifi", "hakdogss")
                with(sharedPreferences.edit()) {
                    putBoolean("isConnected_$userEmail", true)
                    apply()
                }
                startTimer()
                val intent = Intent(this, ConnectedSuccessfully::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Wi-Fi code found! Generate a code fihv 1rst.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }

        buttonGenerateForAnotherDevice.setOnClickListener {
            val intent = Intent(this, connecttootherdevice::class.java)
            startActivity(intent)
        }
    }

    private fun displayAsterisks() {
        for (textView in codeTextViews) {
            textView.text = "*"
        }
    }

    private fun generateAndDisplayCode() {
        val randomCode = Random.nextInt(10000000, 99999999).toString()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Store the code in SharedPreferences (for immediate display)
        with(sharedPreferences.edit()) {
            putString("savedCode_$userEmail", randomCode)
            apply()
        }

        // Store the code in SQLite database
        val dbHelper = DatabaseHelper(this)
        val isStored = dbHelper.storeWiFiCode(userEmail, randomCode)

        if (isStored) {
            Toast.makeText(this, "Wi-Fi Code Stored Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to Store Wi-Fi Code!", Toast.LENGTH_SHORT).show()
        }

        // Display the generated code in the UI
        for (i in randomCode.indices) {
            codeTextViews[i].text = randomCode[i].toString()
        }
    }


    private fun startTimer() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 4 * 60 * 60 * 1000

        with(sharedPreferences.edit()) {
            putLong("startTime_$userEmail", startTime)
            putLong("endTime_$userEmail", endTime)
            apply()
        }

        handler.postDelayed(disconnectRunnable, 4 * 60 * 60 * 1000)
    }

    private fun disconnectWiFi() {
        WifiConnector.disconnectFromWiFi(this)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()) {
            remove("isConnected_$userEmail")
            remove("savedCode_$userEmail")
            apply()
        }
        runOnUiThread {
            Toast.makeText(this, "Wi-Fi disconnected after 4 hours.", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserEmail(): String? {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userEmail", null)
    }
}
