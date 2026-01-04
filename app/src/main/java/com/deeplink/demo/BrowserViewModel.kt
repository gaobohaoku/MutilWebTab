package com.deeplink.demo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class TabInfo(val id: String, val title: String)

class BrowserViewModel : ViewModel() {

    // 所有 Tab 的列表
    val tabs = MutableLiveData<MutableList<TabInfo>>(mutableListOf())

    // 当前选中的 Tab ID
    private val _currentTabId = MutableLiveData<String?>()
    val currentTabId: LiveData<String?> = _currentTabId

    private var counter = 0

    companion object {
        // 定义 Home 页的固定 ID
        const val ID_HOME = "ID_HOME_DASHBOARD"
    }

    fun createNewTab() {
        counter++
        val newTab = TabInfo("tab_$counter", "Page $counter")

        val list = tabs.value ?: mutableListOf()
        list.add(newTab)
        tabs.value = list // 通知 UI 更新 Tab 栏

        // 新建后自动选中
        selectTab(newTab.id)
    }

    fun selectTab(id: String) {
        if (_currentTabId.value != id) {
            _currentTabId.value = id
        }
    }

    // 【新增】删除 Tab 逻辑
    fun removeTab(tabId: String) {
        val currentList = tabs.value ?: return
        val tabToRemove = currentList.find { it.id == tabId } ?: return

        // 1. 获取要删除的位置
        val index = currentList.indexOf(tabToRemove)
        currentList.removeAt(index)

        // 2. 如果删除的是当前选中的 Tab，需要重新计算选中项
        if (_currentTabId.value == tabId) {
            if (currentList.isNotEmpty()) {
                // 优先选中前一个，如果前一个没有，就选中第一个
                val newIndex = if (index > 0) index - 1 else 0
                selectTab(currentList[newIndex].id)
            } else {
                // 如果删光了，置空
                _currentTabId.value = null
            }
        }

        // 3. 通知 UI 更新
        tabs.value = currentList
    }
}