package com.tampilator.editorfoto

import android.content.Context
import android.net.wifi.WifiManager
import android.webkit.JavascriptInterface

class WifiBridge(private val context: Context) {

    // Dipanggil dari JavaScript: window.AndroidBridge.getTvBoxIp()
    // Mengembalikan string kosong "" kalau gagal (editor.html akan pakai IP default sebagai fallback)
    @JavascriptInterface
    fun getTvBoxIp(): String {
        return try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val gateway = wifiManager.dhcpInfo?.gateway ?: 0
            if (gateway == 0) "" else intToIp(gateway)
        } catch (e: Exception) {
            ""
        }
    }

    private fun intToIp(ip: Int): String {
        return "" +
            (ip and 0xFF) + "." +
            (ip shr 8 and 0xFF) + "." +
            (ip shr 16 and 0xFF) + "." +
            (ip shr 24 and 0xFF)
    }
}
