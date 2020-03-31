package com.davidsadler.bluepail.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.util.getDaysAwayFromNow
import com.davidsadler.bluepail.util.resize
import kotlinx.android.synthetic.main.item_plant_list_cell.view.*
import java.util.*

interface OnItemClickedListener {
    fun onItemClicked(selectedPlant: Plant)
}

class PlantsAdapter internal constructor(context: Context, private val itemClickedListener: OnItemClickedListener) : RecyclerView.Adapter<PlantsAdapter.PlantViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var plants = emptyList<Plant>()

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorImageView: ImageView = itemView.imageView_plant_color
        val titleLabel: TextView = itemView.textView_plant_title
        val nextWateringLabel: TextView = itemView.textView_plant_water_date
        val nextFertilizingLabel: TextView = itemView.textView_plant_fertilize_date
        val iconImageView: ImageView = itemView.imageView_plant_photo
        val waterIconImageView: ImageView = itemView.imageView_plant_watered_status
        val fertilizerIconImageView: ImageView = itemView.imageView_plant_fertilized_status
        fun bind(plant: Plant, clickListener: OnItemClickedListener) {
            itemView.setOnClickListener{
                clickListener.onItemClicked(plant)
            }
        }
    }

    override fun getItemCount(): Int {
        return plants.size
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.bind(plant,itemClickedListener)
        holder.titleLabel.text = plant.name
        holder.colorImageView.setBackgroundColor(plant.colorId)
        if (plant.photo != null) {
            val plantPhoto = BitmapFactory.decodeFile(plant.photo).resize(175,100)
            holder.iconImageView.setImageBitmap(plantPhoto)
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_launcher_background)
        }
        holder.nextWateringLabel.text = plant.wateringDate.getDaysAwayFromNow(true)
        if (plant.wateringDate <= Date()) {
            holder.waterIconImageView.setColorFilter(Color.RED)
        } else {
            holder.waterIconImageView.clearColorFilter()
        }
        if (plant.fertilizerDate != null) {
            holder.nextFertilizingLabel.text = plant.fertilizerDate.getDaysAwayFromNow(true)
            if (plant.fertilizerDate <= Date()) {
                holder.fertilizerIconImageView.setColorFilter(Color.RED)
            } else {
                holder.fertilizerIconImageView.clearColorFilter()
            }
        } else {
            holder.nextFertilizingLabel.text = "N/A"
        }
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