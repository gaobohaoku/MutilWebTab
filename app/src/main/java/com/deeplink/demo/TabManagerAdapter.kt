package com.deeplink.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.multi.webtab.demo.R

class TabManagerAdapter(
    private val onDeleteClick: (String) -> Unit,
    private val onSelectClick: (String) -> Unit,
    private val onAddNewClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<TabInfo> = emptyList()
    private var currentSelectedId: String? = null

    // 定义 ViewType
    private val TYPE_ITEM = 0
    private val TYPE_FOOTER = 1

    fun submitList(newList: List<TabInfo>, selectedId: String?) {
        items = newList
        currentSelectedId = selectedId
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun getItemCount(): Int = items.size + 1 // +1 是 Footer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ITEM) {
            val view = inflater.inflate(R.layout.item_tab_manager, parent, false)
            TabViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_tab_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TabViewHolder) {
            val tab = items[position]
            holder.bind(tab, tab.id == currentSelectedId)
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    // --- ViewHolders ---

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_tab_name)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        init {
            // 卡片整体放大
            tvName.setupFocusScale(1.1f)
            // 删除按钮放大
            btnDelete.setupFocusScale(1.2f)
        }


        fun bind(tab: TabInfo, isSelected: Boolean) {
            tvName.text = tab.title
            tvName.isSelected = isSelected

            // 【修改点】如果是 Home Tab，隐藏删除按钮
            if (tab.id == BrowserViewModel.ID_HOME) {
                btnDelete.visibility = View.GONE
                btnDelete.isFocusable = false // 禁止获取焦点
            } else {
                btnDelete.visibility = View.VISIBLE
                btnDelete.isFocusable = true
            }

            tvName.setOnClickListener { onSelectClick(tab.id) }

            // 只有非 Home 才有点击事件
            if (tab.id != BrowserViewModel.ID_HOME) {
                btnDelete.setOnClickListener { onDeleteClick(tab.id) }
            }
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnAdd: Button = itemView.findViewById(R.id.btn_add_new)

        init {
            // 【核心修改】给新建按钮也加上放大效果
            btnAdd.setupFocusScale(1.05f)
        }

        fun bind() {
            btnAdd.setOnClickListener { onAddNewClick() }
        }
    }
}
