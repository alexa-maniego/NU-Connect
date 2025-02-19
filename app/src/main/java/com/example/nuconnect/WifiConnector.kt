package com.example.nuconnect

import android.content.Context
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

object WifiConnector {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWiFi(context: Context, ssid: String, password: String) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Create a WifiNetworkSuggestion
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password) // Use WPA2 security
            .setIsAppInteractionRequired(false) // No user prompt
            .build()

        val suggestionsList = listOf(suggestion)
        val status = wifiManager.addNetworkSuggestions(suggestionsList)

        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(context, "Wi-Fi Connected! Android will connect automatically.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Failed to add Wi-Fi suggestion.", Toast.LENGTH_LONG).show()
            Log.e("WifiConnector", "Failed to add Wi-Fi suggestion: Status Code $status")
        }
    }
    fun disconnectFromWiFi(context: Context) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.removeNetworkSuggestions(emptyList()) // Removes any active network suggestions
    }

}
