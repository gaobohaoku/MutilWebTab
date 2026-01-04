package com.deeplink.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.core.net.toUri

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取按钮控件
        val openYouTubeButton = findViewById<Button>(R.id.openYouTubeButton)
        openYouTubeButton.requestFocus()
        openYouTubeButton.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 焦点时设置按钮背景
                openYouTubeButton.setBackgroundResource(R.drawable.button_focused)
                // 或者增加动画效果
                openYouTubeButton.animate().scaleX(1.1f).scaleY(1.1f).duration = 200
            } else {
                // 失去焦点时恢复默认背景
                openYouTubeButton.setBackgroundResource(R.drawable.button_default)
                // 恢复原来的尺寸
                openYouTubeButton.animate().scaleX(1f).scaleY(1f).duration = 200
            }
        }

        val openOtherAppButton = findViewById<Button>(R.id.openOtherAppButton)
        openOtherAppButton.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 焦点时设置按钮背景
                openOtherAppButton.setBackgroundResource(R.drawable.button_focused)
                // 或者增加动画效果
                openOtherAppButton.animate().scaleX(1.1f).scaleY(1.1f).duration = 200
            } else {
                // 失去焦点时恢复默认背景
                openOtherAppButton.setBackgroundResource(R.drawable.button_default)
                // 恢复原来的尺寸
                openOtherAppButton.animate().scaleX(1f).scaleY(1f).duration = 200
            }
        }

        // 处理打开 YouTube 的 Deep Link 跳转
        openYouTubeButton.setOnClickListener {
            openYouTube("com.seraphic.openinet.demo",OB_SCHEME,OB_MAIN)
        }

        // 处理打开其他应用的 Deep Link 跳转
        openOtherAppButton.setOnClickListener {
            openYoutubeTVWithID("com.seraphic.openinet.demo",OB_SCHEME,OB_MAIN)
        }
    }

    /**
     * 模拟 DeepLink 跳转打开 YouTube TV应用
     * @param bundleName 应用包名
     * @param scheme deeplink协议
     * @param main 应用的首页
     */
    private fun openYouTube(bundleName: String, scheme: String, main: String) {
        val intent = Intent().apply {
            setClassName(bundleName, main)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val jsonData =
                """${scheme}{"type":6,"url":"https://www.youtube.com/tv?metax_mode=focus","key":"0"}"""
            data = jsonData.toUri()
        }

        // 检查是否存在可以处理此 Intent 的应用
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // 处理没有匹配的应用
            showToast("目标应用未安装或未正确设置 Deep Link")
        }
    }

    /**
     * 模拟 DeepLink 携带videoId跳转打开 YouTube TV应用
     * @param bundleName 应用包名
     * @param scheme deeplink协议
     * @param main 应用的首页
     */
    private fun openYoutubeTVWithID(bundleName: String, scheme: String, main: String) {
        val intent = Intent().apply {
            setClassName(bundleName, main)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val jsonData =
                """${scheme}{"type":6,"url":"https://www.youtube.com/tv?metax_mode=focus#/watch?v=HZsRjrYW-lk","key":"0"}"""
            data = jsonData.toUri()
        }

        // 检查是否存在可以处理此 Intent 的应用
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // 处理没有匹配的应用
            showToast("目标应用未安装或未正确设置 Deep Link")
        }
    }

    // 显示 Toast 消息
    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val OB_SCHEME = "seraphic://"
        private const val OB_MAIN = "com.seraphic.openinet.ui.main.MainActivity"

        private const val OBL_SCHEME = "lite://"
        private const val OBL_MAIN = "com.seraphic.lite.aty.MetaXWebTabAty"
    }
}
