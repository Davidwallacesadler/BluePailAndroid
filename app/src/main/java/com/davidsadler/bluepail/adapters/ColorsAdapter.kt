package com.davidsadler.bluepail.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import kotlinx.android.synthetic.main.item_color_list_cell.view.*

interface OnColorSelectedListener {
    fun colorSelected(colorId: Int)
}

class ColorsAdapter internal constructor(val context: Context,private val colorSelectedListener: OnColorSelectedListener) : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    // Default color is red:
    var selectedColor = ContextCompat.getColor(context,R.color.colorGroupRed)

    // TODO: decouple the data source from the adapter
    private var colors = listOf(
        R.color.colorGroupRed,
        R.color.colorGroupOrange,
        R.color.colorGroupYellow,
        R.color.colorGroupGreen,
        R.color.colorGroupBlue,
        R.color.colorGroupPurple,
        R.color.colorGroupMagenta)

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorBackground : CardView = itemView.cardView_cell_color
        val selectedOverlay : ImageView = itemView.imageView_selected_status
        fun bind(colorId: Int, clickListener: OnColorSelectedListener, adapter: ColorsAdapter, position: Int) {
            itemView.setOnClickListener {
                clickListener.colorSelected(colorId)
                selectedColor = colorId
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
       return colors.count()
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorId = colors[position]
        val color = ContextCompat.getColor(context,colorId)
        holder.colorBackground.setCardBackgroundColor(color)
        holder.selectedOverlay.isVisible = color != selectedColor
        holder.bind(color,colorSelectedListener, this, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val cellForRow = inflater.inflate(R.layout.item_color_list_cell, parent, false)
        return ColorViewHolder(cellForRow)
    }

    fun setColors(colorList: List<Int>) {
        colors = colorList
        notifyDataSetChanged()
    }
}