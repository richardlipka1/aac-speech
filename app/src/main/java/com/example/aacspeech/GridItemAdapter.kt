package com.example.aacspeech

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView

class GridItemAdapter(
    private val context: Context,
    private var items: MutableList<GridItem>,
    private val onItemClick: (GridItem) -> Unit,
    private val onDeleteClick: (GridItem) -> Unit,
    private var currentLanguage: String = "en"
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.itemContainer),
                view.findViewById(R.id.itemText),
                view.findViewById(R.id.btnDelete)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = items[position]
        holder.itemText.text = item.getTextForLanguage(currentLanguage)
        holder.itemContainer.setBackgroundColor(item.backgroundColor)

        holder.itemContainer.setOnClickListener {
            Log.d("GridItemAdapter", "Item clicked: ${item.text}")
            onItemClick(item)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }

        return view
    }

    fun updateItems(newItems: MutableList<GridItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setLanguage(language: String) {
        currentLanguage = language
        notifyDataSetChanged()
    }

    private data class ViewHolder(
        val itemContainer: RelativeLayout,
        val itemText: TextView,
        val btnDelete: ImageButton
    )
}
