package com.example.nuconnect

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateanAccount : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createan_account)

        dbHelper = DatabaseHelper(this)

        val firstName = findViewById<EditText>(R.id.editTextFirstname)
        val lastName = findViewById<EditText>(R.id.editLastname)
        val middleName = findViewById<EditText>(R.id.editTextMiddlename)
        val suffix = findViewById<EditText>(R.id.editTextSuffix)
        val email = findViewById<EditText>(R.id.editTextEmail)
        val phoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val birthdate = findViewById<EditText>(R.id.editTextBirthdate)
        val course = findViewById<EditText>(R.id.editTextCourse)
        val yearLevel = findViewById<EditText>(R.id.editTextYearLevel)
        val studentID = findViewById<EditText>(R.id.editTextStudentID)
        val password = findViewById<EditText>(R.id.editTextPassword)
        val confirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val signUpButton = findViewById<Button>(R.id.buttonSignUp)
        val signInButton = findViewById<Button>(R.id.buttonSignIn)

        signUpButton.setOnClickListener {
            if (validateInput(
                    firstName, lastName, middleName, suffix, email, phoneNumber, birthdate, course, yearLevel, studentID, password, confirmPassword
                )) {
                val user = User(
                    firstName = firstName.text.toString(),
                    lastName = lastName.text.toString(),
                    middleName = middleName.text.toString(),
                    suffix = suffix.text.toString(),
                    email = email.text.toString(),
                    phoneNumber = phoneNumber.text.toString(),
                    birthdate = birthdate.text.toString(),
                    course = course.text.toString(),
                    yearLevel = yearLevel.text.toString(),
                    studentID = studentID.text.toString(),
                    password = password.text.toString()
                )

                dbHelper.addUser(user)
                Toast.makeText(this@CreateanAccount, "Account created! Please sign in.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }
        }

        signInButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(
        firstName: EditText, lastName: EditText, middleName: EditText, suffix: EditText,
        email: EditText, phoneNumber: EditText, birthdate: EditText, course: EditText,
        yearLevel: EditText, studentID: EditText, password: EditText, confirmPassword: EditText
    ): Boolean {
        if (firstName.text.isBlank()) {
            showToast("First name is required")
            return false
        }
        if (lastName.text.isBlank()) {
            showToast("Last name is required")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
            showToast("Enter a valid email")
            return false
        }
        if (!validatePhoneNumber(phoneNumber.text.toString())) {
            showToast("Enter a valid phone number (10-15 digits)")
            return false
        }
        if (!validateBirthdate(birthdate.text.toString())) {
            showToast("Enter a valid birthdate (dd/MM/yyyy)")
            return false
        }
        if (!studentID.text.matches(Regex("^(20[0-9]{2})-\\d{6}$"))) {
            showToast("Enter a valid Student ID (YYYY-NNNNNN)")
            return false
        }
        if (password.text.toString() != confirmPassword.text.toString()) {
            showToast("Passwords do not match")
            return false
        }
        if (password.text.length < 6) {
            showToast("Password should be at least 6 characters long")
            return false
        }
        if (course.text.isBlank()) {
            showToast("Course is required")
            return false
        }
        if (yearLevel.text.isBlank()) {
            showToast("Year level is required")
            return false
        }
        return true
    }

    private fun validatePhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^\\d{10,15}$")) // Only digits, 10-15 characters long
    }

    private fun validateBirthdate(birthdate: String): Boolean {
        if (!birthdate.matches(Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19[0-9]{2}|20[0-9]{2})$"))) {
            return false
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false // Ensures strict date parsing

        return try {
            dateFormat.parse(birthdate) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
