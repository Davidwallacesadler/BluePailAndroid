package com.davidsadler.bluepail.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.util.dryRed
import com.davidsadler.bluepail.util.fertilizerGreen
import com.davidsadler.bluepail.util.getDaysAwayFromNow
import com.davidsadler.bluepail.util.wateredBlue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_plant_list_cell.view.*
import java.util.*

interface OnItemClickedListener {
    fun onItemClicked(selectedPlant: Plant)
}

class PlantsAdapter internal constructor(context: Context, private val itemClickedListener: OnItemClickedListener) : RecyclerView.Adapter<PlantsAdapter.PlantViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var plants = emptyList<Plant>()

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorImageView: ImageView = itemView.imageView_plant_color
        private val titleLabel: TextView = itemView.textView_plant_title
        private val nextWateringLabel: TextView = itemView.textView_plant_water_date
        private val nextFertilizingLabel: TextView = itemView.textView_plant_fertilize_date
        private val iconImageView: ImageView = itemView.imageView_plant_photo
        private val waterIconImageView: ImageView = itemView.imageView_plant_watered_status
        private val fertilizerIconImageView: ImageView = itemView.imageView_plant_fertilized_status
        private val fertilizerLayout: LinearLayout = itemView.linearLayout_fertilizer
        private fun bind(plant: Plant, clickListener: OnItemClickedListener) {
            itemView.setOnClickListener{
                clickListener.onItemClicked(plant)
            }
        }
        fun setupCellWithPlant(plant: Plant) {
            bind(plant,itemClickedListener)
            titleLabel.text = plant.name
            colorImageView.setBackgroundColor(plant.colorId)
            if (plant.photo != null) {
                val picasso = Picasso.get()
                val photoString = plant.photo
                val photoUri = Uri.parse("file://$photoString")
                picasso
                    .load(photoUri).resize(150,150)
                    .placeholder(R.drawable.item_view_default_plant_photo)
                    .error(R.drawable.fab_plus)
                    .into(iconImageView)
            } else {
                iconImageView.setImageResource(R.drawable.item_view_default_plant_photo)
            }
            nextWateringLabel.text = plant.wateringDate.getDaysAwayFromNow(true)
            if (plant.wateringDate <= Date()) {
                waterIconImageView.setColorFilter(Color().dryRed())
            } else {
                waterIconImageView.setColorFilter(Color().wateredBlue())
            }
            if (plant.fertilizerDate != null) {
                fertilizerLayout.isVisible = true
                nextFertilizingLabel.text = plant.fertilizerDate.getDaysAwayFromNow(true)
                if (plant.fertilizerDate <= Date()) {
                    fertilizerIconImageView.setColorFilter(Color().dryRed())
                } else {
                    fertilizerIconImageView.setColorFilter(Color().fertilizerGreen())
                }
            } else {
                fertilizerLayout.isVisible = false
            }
        }
    }

    override fun getItemCount(): Int {
        return plants.size
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.setupCellWithPlant(plant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val cellForRow = inflater.inflate(R.layout.item_plant_list_cell, parent, false)
        return PlantViewHolder(cellForRow)
    }

    internal fun setPlants(plants: List<Plant>) {
        this.plants = plants
        notifyDataSetChanged()
    }
}