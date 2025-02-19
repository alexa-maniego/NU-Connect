package com.example.nuconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConnectedSuccessfully : AppCompatActivity() {

    private lateinit var backButtonCopy: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connected_successfully)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val continueButton = findViewById<Button>(R.id.button3)
        continueButton.setOnClickListener {
            // Change this to navigate to MainActivity or the intended activity
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)

        }
        backButtonCopy = findViewById(R.id.backbutton_copy)
        backButtonCopy.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }
    }
}
