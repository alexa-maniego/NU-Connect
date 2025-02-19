package com.example.nuconnect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.widget.TextView
import androidx.core.content.ContextCompat

class WifiStateReceiver(private val statusTextView: TextView, private val context: Context) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            val isConnected = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            val isWifiEnabled = wifiManager.isWifiEnabled  // Ensures Wi-Fi is actually enabled

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val email = getUserEmail(context) ?: ""

            // Save connection status in SharedPreferences
            sharedPreferences.edit().putBoolean("isConnected_${email}", isConnected && isWifiEnabled).apply()

            // Delay UI update to prevent flickering
            Handler(Looper.getMainLooper()).postDelayed({
                if (isConnected && isWifiEnabled) {
                    statusTextView.text = "Connected"
                    statusTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                } else {
                    statusTextView.text = "Not Connected"
                    statusTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                }
            }, 1000) // 1-second delay for stability
        }
    }

    private fun getUserEmail(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userEmail", null)
    }
}
