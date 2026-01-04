package com.deeplink.demo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.multi.webtab.demo.R
import com.multi.webtab.demo.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: BrowserViewModel by activityViewModels()
    private var binding: FragmentHomeBinding? = null
    private var lastVisibleTabId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        binding?.btnAddTab?.setupFocusScale(1.1f)
        // 1. 添加按钮点击
        binding?.btnAddTab?.setOnClickListener {
            viewModel.createNewTab()
        }

        binding?.btnManager?.setupFocusScale(1.1f)
        // 跳转到管理页面
        binding?.btnManager?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_manager)
        }

        // 2. 监听 Tab 数据变化
        viewModel.tabs.observe(viewLifecycleOwner) { tabList ->
            refreshTabButtons(tabList)
        }

        // 【新增】监听 Home 页面中间的大按钮
        binding?.btnCreateTabFromHome?.setOnClickListener {
            // 1. 在 ViewModel 中创建新数据
            viewModel.createNewTab()
            // 2. ViewModel 内部会自动将 currentTabId 设为新 Tab 的 ID
            // 3. LiveData 监听到 ID 变化 -> 触发 performTabSwitch -> 界面自动替换为 WebFragment
        }

        viewModel.currentTabId.observe(viewLifecycleOwner) { tabId ->
            if (tabId != null) {
                // 有 Tab 选中：正常显示
                binding?.childFragmentContainer?.visibility = View.VISIBLE
                performTabSwitch(tabId)
                updateTabVisualState(tabId)
            } else {
                // 【新增】无 Tab 选中（空状态）：
                // 1. 隐藏子容器（显示背景）
                binding?.childFragmentContainer?.visibility = View.GONE
                // 2. 隐藏所有的 Tab 按钮
                binding?.llTabStrip?.removeAllViews()

                // 3. 可以在这里显示一个 "空空如也，请点击管理新建" 的 TextView
                // 或者自动让焦点聚焦到 "管理" 按钮上
                binding?.btnManager?.requestFocus()
            }
        }
    }

    private fun refreshTabButtons(tabs: MutableList<TabInfo>) {
        val container = binding?.llTabStrip ?: return
        container.removeAllViews()

        tabs.forEach { tab ->
            val btn = Button(requireContext()).apply {
                text = tab.title
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.white))

                // 【TV 关键设置】
                isFocusable = true
                isFocusableInTouchMode = true
                // 设置我们在 XML 定义的 Selector 背景
                setBackgroundResource(R.drawable.button_focus_state)

                // 布局参数：增加一点间距
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16
                }

                // 点击事件：切换 ViewModel 状态
                setOnClickListener {
                    viewModel.selectTab(tab.id)
                }

                // 【TV 体验优化】获得焦点时，稍微放大一点（呼吸感）
                setOnFocusChangeListener { v, hasFocus ->
                    val scale = if (hasFocus) 1.1f else 1.0f
                    v.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
                }
            }
            container.addView(btn)
        }

        // 刷新 UI 状态
        viewModel.currentTabId.value?.let { updateTabVisualState(it) }

        // 【TV 体验优化】如果是新建的 Tab，尝试请求焦点
        // 简单的逻辑：如果当前没有焦点，或者焦点在 Add 按钮上，且刚加了新 Tab，则聚焦到最后一个
        if (tabs.isNotEmpty()) {
            container.getChildAt(tabs.size - 1).requestFocus()
        }
    }

    // 更新 Tab 的视觉状态 (Selected vs Unselected)
    // 注意：Focus 状态由系统和 xml 自动管理，这里只管理 "Logic Selected"
    private fun updateTabVisualState(currentId: String) {
        val container = binding?.llTabStrip ?: return
        val tabs = viewModel.tabs.value ?: return

        for (i in 0 until container.childCount) {
            val btn = container.getChildAt(i)
            val tabId = tabs.getOrNull(i)?.id

            // 设置 view.isSelected，这会触发 xml 中的 android:state_selected="true"
            // 从而改变背景颜色为深蓝色（即使用户焦点不在这个按钮上）
            btn.isSelected = (tabId == currentId)
        }
    }

    private fun performTabSwitch(targetTabId: String) {
        val childFM = childFragmentManager
        val transaction = childFM.beginTransaction()

        // --- 第一步：处理“上一个”显示的 Web Fragment ---
        // 无论我们要去哪，都得先把当前的 Web Fragment 隐藏（如果有的话）
        if (lastVisibleTabId != null && lastVisibleTabId != BrowserViewModel.ID_HOME) {
            val lastFragment = childFM.findFragmentByTag(lastVisibleTabId)
            if (lastFragment != null && lastFragment.isAdded) {
                transaction.hide(lastFragment)
                transaction.setMaxLifecycle(lastFragment, Lifecycle.State.STARTED) // 暂停它
            }
        }

        // --- 第二步：处理“目标”视图 ---
        if (targetTabId == BrowserViewModel.ID_HOME) {
            // 【场景 A】：切换到原生 Home

            // 1. 显示原生 Layout
            binding?.layoutNativeHome?.visibility = View.VISIBLE
            // 2. 隐藏 Fragment 容器 (视觉上隐藏，实际上 hide 事务已经处理了 Fragment 对象)
            binding?.childFragmentContainer?.visibility = View.GONE

        } else {
            // 【场景 B】：切换到 Web Tab

            // 1. 隐藏原生 Layout
            binding?.layoutNativeHome?.visibility = View.GONE
            // 2. 显示 Fragment 容器
            binding?.childFragmentContainer?.visibility = View.VISIBLE

            // 3. 标准的 Fragment 查找/添加/显示逻辑
            var targetFragment = childFM.findFragmentByTag(targetTabId)

            if (targetFragment == null) {
                val tabInfo = viewModel.tabs.value?.find { it.id == targetTabId }
                val title = tabInfo?.title ?: "Page"

                targetFragment = WebFragment.newInstance(title)
                transaction.add(R.id.child_fragment_container, targetFragment, targetTabId)
                transaction.setMaxLifecycle(targetFragment, Lifecycle.State.RESUMED)
            } else {
                transaction.show(targetFragment)
                transaction.setMaxLifecycle(targetFragment, Lifecycle.State.RESUMED)
            }
        }

        transaction.commitNow()
        lastVisibleTabId = targetTabId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}