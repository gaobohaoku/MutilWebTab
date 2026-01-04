package com.deeplink.demo

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import com.multi.webtab.demo.R

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

/**
 * 为 TV 元素添加选中放大效果
 * @param scaleRatio 放大倍数，默认 1.1 倍
 */
fun View.setupFocusScale(scaleRatio: Float = 1.1f) {
    this.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            // 获得焦点：放大 + 提升层级(Z轴)
            v.animate()
                .scaleX(scaleRatio)
                .scaleY(scaleRatio)
                .setDuration(200) // 动画时长
                .start()
            // 提升层级，防止被旁边的 Item 遮挡
            ViewCompat.setElevation(v, 10f)
        } else {
            // 失去焦点：复原
            v.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(200)
                .start()
            ViewCompat.setElevation(v, 0f)
        }
    }
}