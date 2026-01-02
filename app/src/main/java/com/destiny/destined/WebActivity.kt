package com.destiny.destined

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        
        val webView = findViewById<WebView>(R.id.webview)
        
        // 启用WebView调试（仅在开发环境使用）
        WebView.setWebContentsDebuggingEnabled(true)
        
        // 配置WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true  // 启用JavaScript
        webSettings.domStorageEnabled = true  // 启用DOM存储
        webSettings.allowFileAccess = true    // 允许访问文件
        webSettings.allowContentAccess = true // 允许内容访问
        webSettings.allowFileAccessFromFileURLs = true // 允许从文件URL访问其他文件
        webSettings.allowUniversalAccessFromFileURLs = true // 允许从文件URL访问任何来源
        
        // 支持ES6模块
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.javaScriptEnabled = true
        }
        
        // 支持混合内容（http和https混合）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        
        // 启用缓存
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true

        // 设置WebViewClient处理页面加载和错误
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebActivity", "页面开始加载: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebActivity", "页面加载完成: $url")
            }
            
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                
                val requestUrl = request?.url?.toString() ?: "未知URL"
                val errorCode = error?.errorCode ?: -1
                val errorDesc = error?.description?.toString() ?: "未知错误"
                
                Log.e("WebActivity", "资源加载错误: URL=$requestUrl, 错误代码=$errorCode, 错误描述=$errorDesc")
            }
            
            @Deprecated("Deprecated in Java")
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebActivity", "旧版错误: URL=$failingUrl, 错误代码=$errorCode, 错误描述=$description")
            }
            
            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                
                val requestUrl = request?.url?.toString() ?: "未知URL"
                val statusCode = errorResponse?.statusCode ?: -1
                val reasonPhrase = errorResponse?.reasonPhrase ?: "未知原因"
                
                Log.e("WebActivity", "HTTP错误: URL=$requestUrl, 状态码=$statusCode, 原因=$reasonPhrase")
            }
        }
        
        // 设置WebChromeClient处理JavaScript对话框和控制台消息
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                val message = consoleMessage?.message() ?: "空消息"
                val sourceId = consoleMessage?.sourceId() ?: "未知来源"
                val lineNumber = consoleMessage?.lineNumber() ?: -1
                
                Log.d("WebActivity JS", "$message -- 来自 $sourceId 的第 $lineNumber 行")
                return super.onConsoleMessage(consoleMessage)
            }
            
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                Log.d("WebActivity JS Alert", "URL: $url, Message: $message")
                return super.onJsAlert(view, url, message, result)
            }
        }
        
        // 加载本地HTML文件
        webView.loadUrl("file:///android_asset/dist/index.html")
    }
}