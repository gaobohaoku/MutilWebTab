package com.deeplink.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.multi.webtab.demo.R

class TabManagerFragment : Fragment(R.layout.fragment_tab_manager) {

    private val viewModel: BrowserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_tabs)
        // 【核心修改】设置为 HORIZONTAL
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val adapter = TabManagerAdapter(
            onDeleteClick = { tabId ->
                // 调用 ViewModel 删除数据，LiveData 更新会自动刷新 Adapter
                viewModel.removeTab(tabId)
            },
            onSelectClick = { tabId ->
                // 选中 Tab
                viewModel.selectTab(tabId)
                // 选中后关闭当前页面，回到 Home
                findNavController().popBackStack()
            },
            // 【核心修改】
            onAddNewClick = {
                // 1. 选中 Home Tab (ID_HOME)
                viewModel.selectTab(BrowserViewModel.ID_HOME)

                // 2. 直接返回 (回到 HomeFragment，此时 HomeFragment 会显示原生 Dashboard)
                findNavController().popBackStack()
            }
        )

        recyclerView.adapter = adapter

        // 监听数据变化刷新列表
        viewModel.tabs.observe(viewLifecycleOwner) { tabs ->
            adapter.submitList(tabs, viewModel.currentTabId.value)
        }

        // 监听选中项变化刷新列表（主要为了更新高亮状态）
        viewModel.currentTabId.observe(viewLifecycleOwner) { id ->
            adapter.submitList(viewModel.tabs.value ?: emptyList(), id)
        }
    }
}