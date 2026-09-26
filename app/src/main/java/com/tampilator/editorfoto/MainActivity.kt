package com.tampilator.editorfoto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val KODE_PILIH_FOTO = 51426

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // "AndroidBridge" ini yang dipanggil dari editor.html lewat
        // window.AndroidBridge.getTvBoxIp()
        webView.addJavascriptInterface(WifiBridge(this), "AndroidBridge")
        webView.webViewClient = WebViewClient()

        // WebChromeClient dibutuhkan supaya <input type="file" accept="image/*">
        // di editor.html bisa memicu galeri foto Android. Tanpa ini, tombol
        // pilih foto di HTML tidak akan bereaksi sama sekali.
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.filePathCallback?.onReceiveValue(null)
                this@MainActivity.filePathCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }
                try {
                    startActivityForResult(
                        Intent.createChooser(intent, "Pilih Foto"),
                        KODE_PILIH_FOTO
                    )
                } catch (e: Exception) {
                    this@MainActivity.filePathCallback = null
                    return false
                }
                return true
            }
        }

        webView.loadUrl("file:///android_asset/editor.html")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == KODE_PILIH_FOTO) {
            val hasil = if (resultCode == Activity.RESULT_OK && data?.data != null) {
                arrayOf(data.data!!)
            } else {
                null
            }
            filePathCallback?.onReceiveValue(hasil)
            filePathCallback = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
