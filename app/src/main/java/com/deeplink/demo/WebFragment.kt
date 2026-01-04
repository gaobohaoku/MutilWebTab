package com.deeplink.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.multi.webtab.demo.R
import com.multi.webtab.demo.databinding.FragmentWebBinding

class WebFragment : Fragment(R.layout.fragment_web) {

    private var binding: FragmentWebBinding? = null
    private var tabTitle: String? = null

    companion object {
        fun newInstance(title: String): WebFragment {
            val fragment = WebFragment()
            val args = Bundle()
            args.putString("title", title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWebBinding.bind(view)

        tabTitle = arguments?.getString("title")
        binding?.tvTitle?.text = tabTitle

        // 证明 View 没有被销毁重建
        binding?.tvStatus?.text = "Created at: ${System.currentTimeMillis()}\nHash: ${this.hashCode()}"
    }

    override fun onResume() {
        super.onResume()
        Log.d("BrowserLifecycle", "Tab [$tabTitle] onResume: >>> 开始运行 (视频/JS)")
        // 这里可以恢复视频播放
    }

    override fun onPause() {
        super.onPause()
        Log.d("BrowserLifecycle", "Tab [$tabTitle] onPause: ||| 暂停运行 (视频/JS)")
        // 这里必须暂停视频播放、停止高频操作
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}