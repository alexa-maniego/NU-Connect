package com.example.nuconnect

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Pofile : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pofile)

        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageViewAvatar = findViewById<ImageView>(R.id.imageViewAvatar)
        loadAvatar(imageViewAvatar)

        imageViewAvatar.setOnClickListener {
            showAvatarSelectionDialog()
        }

        val userEmail = getUserEmail()
        if (userEmail != null) {
            val user = dbHelper.getUserByEmail(userEmail)
            if (user != null) {
                // Set user data to TextViews
                val textViewName = findViewById<TextView>(R.id.textViewProfile)
                textViewName.text = "${user.firstName} ${user.middleName} ${user.lastName} ${user.suffix}"

                val textViewEmail = findViewById<TextView>(R.id.Email)
                textViewEmail.text = user.email

                val textViewStudentID = findViewById<TextView>(R.id.studentID)
                textViewStudentID.text = user.studentID

                val textViewCourseYear = findViewById<TextView>(R.id.CourseYear)
                textViewCourseYear.text = "${user.course} / ${user.yearLevel}"

                // Set phone number and birthdate
                val textViewPhone = findViewById<TextView>(R.id.Phone)
                textViewPhone.text = user.phoneNumber

                val textViewBirthdate = findViewById<TextView>(R.id.bdate)
                textViewBirthdate.text = user.birthdate
            }
        }

        val oldPasswordEditText = findViewById<EditText>(R.id.editTextOldpassword)
        val newPasswordEditText = findViewById<EditText>(R.id.editTextNewpassword)
        val confirmPasswordButton = findViewById<Button>(R.id.confirmPass)

        confirmPasswordButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            if (validatePasswords(oldPassword, newPassword, userEmail)) {
                dbHelper.updateUserPassword(userEmail!!, newPassword)
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                // Optional: Clear the password fields
                oldPasswordEditText.text.clear()
                newPasswordEditText.text.clear()
            }
        }

        val homeButton = findViewById<ImageButton>(R.id.imageHomeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<ImageButton>(R.id.imageSettingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }

    private fun showAvatarSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_avatar, null)
        val avatar1 = dialogView.findViewById<ImageView>(R.id.avatar1)
        val avatar2 = dialogView.findViewById<ImageView>(R.id.avatar2)
        val avatar3 = dialogView.findViewById<ImageView>(R.id.avatar3)
        val avatar4 = dialogView.findViewById<ImageView>(R.id.avatar4)
        val avatar5 = dialogView.findViewById<ImageView>(R.id.avatar5)
        val avatar6 = dialogView.findViewById<ImageView>(R.id.avatar6)

        val avatars = listOf(avatar1, avatar2, avatar3, avatar4, avatar5, avatar6)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        avatars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                saveAvatar(index + 1)  // Adjust index to match the drawable resources
                loadAvatar(findViewById(R.id.imageViewAvatar))
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun saveAvatar(index: Int) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("userAvatar", index)
        editor.apply()
    }

    private fun loadAvatar(imageView: ImageView) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val avatarIndex = sharedPreferences.getInt("userAvatar", 0)
        val avatarDrawable = when (avatarIndex) {
            1 -> R.drawable.ic_avatar1
            2 -> R.drawable.ic_avatar2
            3 -> R.drawable.ic_avatar3
            4 -> R.drawable.ic_avatar4
            5 -> R.drawable.ic_avatar5
            6 -> R.drawable.ic_avatar6
            else -> R.drawable.ic_avatar_default
        }
        imageView.setImageResource(avatarDrawable)
    }

    private fun validatePasswords(oldPassword: String, newPassword: String, userEmail: String?): Boolean {
        val user = dbHelper.getUserByEmail(userEmail!!)
        return if (user?.password == oldPassword) {
            if (newPassword.length >= 6) {
                true
            } else {
                showToast("New password must be at least 6 characters long")
                false
            }
        } else {
            showToast("Old password is incorrect")
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getUserEmail(): String? {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userEmail", null)
    }
}
