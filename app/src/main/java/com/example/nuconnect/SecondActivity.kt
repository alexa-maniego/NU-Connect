package com.example.nuconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        dbHelper = DatabaseHelper(this)

        val signInButton = findViewById<Button>(R.id.buttonSIGNIn)
        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val showPasswordButton = findViewById<ImageButton>(R.id.showPasswordButton)

        var isPasswordVisible = false

        // Toggle password visibility
        showPasswordButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Show password
                passwordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.drawable.view)  // Use eye open icon drawable
            } else {
                // Hide password
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.drawable.hide)  // Use eye closed icon drawable
            }
            // Move the cursor to the end of the text
            passwordField.setSelection(passwordField.text.length)
        }

        signInButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            if (validateInput(email, password)) {
                val user = dbHelper.getUser(email, password)
                if (user != null) {
                    saveUserEmail(email)  // Save user email in SharedPreferences
                    saveLoginState(true)  // Save login state in SharedPreferences
                    val intent = Intent(this, Welcomepage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val buttonCreateAccount = findViewById<Button>(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            val intent = Intent(this, CreateanAccount::class.java)
            startActivity(intent)
        }

        val buttonMicrosoftAccount = findViewById<Button>(R.id.buttonMicrosoftAccount)
        buttonMicrosoftAccount.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address")
            return false
        }
        if (password.isBlank()) {
            showToast("Password is required")
            return false
        }
        if (password.length < 6) {
            showToast("Password should be at least 6 characters long")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserEmail(email: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userEmail", email)
        editor.apply()
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }
    }
}
