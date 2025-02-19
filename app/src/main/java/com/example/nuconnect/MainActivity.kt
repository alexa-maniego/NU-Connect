package com.example.nuconnect

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            // Check if the user is logged in
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // User is logged in, go to Welcomepage
                val intent = Intent(this, Welcomepage::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, go to SecondActivity (login)
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
