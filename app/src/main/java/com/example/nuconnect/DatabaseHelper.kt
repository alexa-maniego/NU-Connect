package com.example.nuconnect

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL(
            """CREATE TABLE users (
            id INTEGER PRIMARY KEY, 
            firstName TEXT, 
            lastName TEXT, 
            middleName TEXT, 
            suffix TEXT, 
            email TEXT UNIQUE, 
            phoneNumber TEXT, 
            birthdate TEXT, 
            course TEXT, 
            yearLevel TEXT, 
            studentID TEXT, 
            password TEXT)"""
        )

        db.execSQL(
            """CREATE TABLE wifi_codes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT UNIQUE, -- Each user gets one active code
            code TEXT UNIQUE,
            status INTEGER DEFAULT 0, -- 0 = unused, 1 = used
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY(email) REFERENCES users(email))"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS wifi_codes")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "nuconnect.db"
        private const val TAG = "DatabaseHelper"
    }

    // Add User
    fun addUser(user: User) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("firstName", user.firstName)
            put("lastName", user.lastName)
            put("middleName", user.middleName)
            put("suffix", user.suffix)
            put("email", user.email)
            put("phoneNumber", user.phoneNumber)
            put("birthdate", user.birthdate)
            put("course", user.course)
            put("yearLevel", user.yearLevel)
            put("studentID", user.studentID)
            put("password", user.password)
        }
        val result = db.insert("users", null, contentValues)
        db.close()

        if (result == -1L) Log.e(TAG, "Failed to insert user: ${user.email}")
    }

    // Get User for Login
    fun getUser(email: String, password: String): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", arrayOf(email, password))
        val user = cursorToUser(cursor)
        cursor.close()
        db.close()
        return user
    }

    // Get User by Email
    fun getUserByEmail(email: String): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))
        val user = cursorToUser(cursor)
        cursor.close()
        db.close()
        return user
    }

    // Convert Cursor to User
    private fun cursorToUser(cursor: Cursor): User? {
        return if (cursor.moveToFirst()) {
            User(
                firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                middleName = cursor.getString(cursor.getColumnIndexOrThrow("middleName")),
                suffix = cursor.getString(cursor.getColumnIndexOrThrow("suffix")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")),
                birthdate = cursor.getString(cursor.getColumnIndexOrThrow("birthdate")),
                course = cursor.getString(cursor.getColumnIndexOrThrow("course")),
                yearLevel = cursor.getString(cursor.getColumnIndexOrThrow("yearLevel")),
                studentID = cursor.getString(cursor.getColumnIndexOrThrow("studentID")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            )
        } else null
    }

    // Update User Password
    fun updateUserPassword(email: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("password", newPassword)
        }
        val result = db.update("users", contentValues, "email = ?", arrayOf(email))
        db.close()
        return result > 0
    }

    // Store a generated Wi-Fi code for a user
    fun storeWiFiCode(email: String, code: String): Boolean {
        val db = this.writableDatabase

        // Before inserting a new code, remove any existing active code for the user
        db.execSQL("DELETE FROM wifi_codes WHERE email = ?", arrayOf(email))

        val values = ContentValues().apply {
            put("email", email)
            put("code", code)
            put("status", 0)     // 0 = Unused
        }

        val result = db.insert("wifi_codes", null, values)
        db.close()

        return result != -1L  // Return true if insertion was successful
    }
}
