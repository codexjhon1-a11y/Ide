package com.weareengineers.waeide

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import org.json.JSONObject
import java.io.File

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Python.isStarted()) Python.start(AndroidPlatform(this))

        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(Bridge(), "WAE")
        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class Bridge {
        @JavascriptInterface
        fun runPython(code: String): String {
            return try {
                val result = Python.getInstance()
                    .getModule("wae_runner")
                    .callAttr("run_code", code)
                    .toString()
                result
            } catch (e: Exception) {
                JSONObject()
                    .put("stdout", "")
                    .put("stderr", e.toString())
                    .put("line", 1)
                    .toString()
            }
        }

        @JavascriptInterface
        fun saveFile(name: String, contents: String): String {
            return try {
                val safeName = name.substringAfterLast('/').substringAfterLast('\\')
                    .filter { it.isLetterOrDigit() || it in "._-" }
                    .ifBlank { "main.py" }
                val dir = File(filesDir, "projects").apply { mkdirs() }
                File(dir, safeName).writeText(contents)
                JSONObject().put("ok", true).put("name", safeName).toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("error", e.toString()).toString()
            }
        }

        @JavascriptInterface
        fun listFiles(): String {
            return try {
                val dir = File(filesDir, "projects").apply { mkdirs() }
                val names = dir.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
                JSONObject().put("ok", true).put("files", org.json.JSONArray(names)).toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("error", e.toString()).toString()
            }
        }

        @JavascriptInterface
        fun readFile(name: String): String {
            return try {
                val safeName = name.substringAfterLast('/').substringAfterLast('\\')
                    .filter { it.isLetterOrDigit() || it in "._-" }
                    .ifBlank { "main.py" }
                val file = File(File(filesDir, "projects"), safeName)
                if (!file.exists()) JSONObject().put("ok", false).put("error", "File not found: $safeName").toString()
                else JSONObject().put("ok", true).put("name", safeName).put("contents", file.readText()).toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("error", e.toString()).toString()
            }
        }

        @JavascriptInterface
        fun deleteFile(name: String): String {
            return try {
                val safeName = name.substringAfterLast('/').substringAfterLast('\\')
                    .filter { it.isLetterOrDigit() || it in "._-" }
                val file = File(File(filesDir, "projects"), safeName)
                JSONObject().put("ok", file.exists() && file.delete()).put("name", safeName).toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("error", e.toString()).toString()
            }
        }

        @JavascriptInterface
        fun openTermux(command: String): String {
            return try {
                val intent = Intent("com.termux.RUN_COMMAND").apply {
                    setClassName("com.termux", "com.termux.app.RunCommandService")
                    putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash")
                    putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("-lc", command))
                    putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
                    putExtra("com.termux.RUN_COMMAND_BACKGROUND", false)
                }
                startService(intent)
                JSONObject().put("ok", true)
                    .put("message", "Command sent to Termux. See Termux for output.")
                    .toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false)
                    .put("error", "Termux integration needs Termux installed and RUN_COMMAND permission enabled. " + e.toString())
                    .toString()
            }
        }

        @JavascriptInterface
        fun hasTermux(): Boolean {
            return try {
                packageManager.getPackageInfo("com.termux", 0)
                true
            } catch (_: Exception) { false }
        }
    }
}
