package com.example.nuconnect

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class connecttootherdevice : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var buttonGenerateCode: Button
    private lateinit var copyButton: ImageButton
    private lateinit var codeTextViews: Array<TextView>
    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connecttootherdevice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backButton = findViewById(R.id.backButton)
        buttonGenerateCode = findViewById(R.id.buttonGenerateCode)
        copyButton = findViewById(R.id.copybut)

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        backButton.setOnClickListener {
            val intent = Intent(this, GenerateCode::class.java)
            startActivity(intent)
        }

        buttonGenerateCode.setOnClickListener {
            generateAndDisplayCode()
        }

        copyButton.setOnClickListener {
            copyCodeToClipboard()
        }

        // Initialize TextView array for the 8-digit code
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
    }

    private fun displayAsterisks() {
        for (textView in codeTextViews) {
            textView.text = "*"
        }
    }

    private fun generateAndDisplayCode() {
        val randomCode = Random.nextInt(10000000, 99999999).toString() // Generate 8-digit number

        for (i in randomCode.indices) {
            codeTextViews[i].text = randomCode[i].toString() // Assign each digit to a TextView
        }
    }

    private fun copyCodeToClipboard() {
        val code = codeTextViews.joinToString("") { it.text.toString() }

        if (code.contains("*")) {
            Toast.makeText(this, "Generate a code first", Toast.LENGTH_SHORT).show()
            return
        }

        val clipData = ClipData.newPlainText("WiFi Code", code)
        clipboardManager.setPrimaryClip(clipData)

        // Show notification (Toast message)
        Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
