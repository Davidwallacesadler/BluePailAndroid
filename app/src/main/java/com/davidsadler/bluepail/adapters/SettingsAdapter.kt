package com.davidsadler.bluepail.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import kotlinx.android.synthetic.main.item_image_and_text_cell.view.*
import kotlinx.android.synthetic.main.item_switch_cell.view.*

class SettingsAdapter(val context: Context, private val switchListener: OnSwitchToggledListener, private val cellTappedListener: OnSettingsCellClickedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val settingTitles = listOf("Dark Mode")
//    , "Rate and Review", "Share"
    private val settingIcons = listOf(R.drawable.image_view_dark_mode_icon)
    //        R.drawable.image_view_rate_review_icon,
    //        R.drawable.image_view_share_icon
    var isDarkModeEnabled = false

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            SettingsCellType.SWITCH.rawValue
        } else {
            SettingsCellType.TEXT.rawValue
        }
    }

    override fun getItemCount(): Int {
        return settingTitles.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return if (viewType == SettingsCellType.SWITCH.rawValue) {
            val switchCellView = inflater.inflate(R.layout.item_switch_cell, parent, false)
            SwitchCellViewHolder(switchCellView)
        } else {
            val textCellView = inflater.inflate(R.layout.item_image_and_text_cell, parent, false)
            TextCellViewHolder(textCellView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == SettingsCellType.SWITCH.rawValue) {
            val switchCellHolder = holder as SwitchCellViewHolder
            switchCellHolder.primarySwitch.text = settingTitles[position]
            switchCellHolder.primarySwitch.isChecked = isDarkModeEnabled
            switchCellHolder.iconImageView.setImageResource(settingIcons[position])
            switchCellHolder.bind(position, switchListener)
        } else {
            val textCellHolder = holder as TextCellViewHolder
            textCellHolder.primaryTextView.text = settingTitles[position]
            textCellHolder.iconImageView.setImageResource(settingIcons[position])
            textCellHolder.bind(position, cellTappedListener)
        }
    }
}

enum class SettingsCellType(val rawValue: Int) {
    SWITCH(0),
    TEXT(1)
}

class SwitchCellViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val iconImageView: ImageView = itemView.imageView_switch_cell
    val primarySwitch: Switch = itemView.switch_for_cell
    fun bind(cellPosition: Int, switchListener: OnSwitchToggledListener) {
        primarySwitch.setOnCheckedChangeListener { _, isChecked ->
            switchListener.onSwitchToggled(cellPosition, isChecked)
        }
    }
}

class TextCellViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val iconImageView: ImageView = itemView.imageView_text_cell
    val primaryTextView: TextView = itemView.textView_text_cell
    fun bind(cellPosition: Int, clickListener: OnSettingsCellClickedListener) {
        itemView.setOnClickListener {
            clickListener.onSettingsCellClicked(cellPosition)
        }
    }
}

interface OnSwitchToggledListener {
    fun onSwitchToggled(position: Int,state: Boolean)
}

interface OnSettingsCellClickedListener {
    fun onSettingsCellClicked(position: Int)
}