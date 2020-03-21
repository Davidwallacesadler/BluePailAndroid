package com.davidsadler.bluepail.fragments

import android.os.Bundle
import android.view.*
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.ColorsAdapter
import com.davidsadler.bluepail.adapters.OnColorSelectedListener
import com.davidsadler.bluepail.util.getDateAtDesiredTime
import kotlinx.android.synthetic.main.fragment_plant_detail.*
import java.util.*

class PlantDetail : Fragment(), OnColorSelectedListener, OnReminderUpdatedListener, TimePicker.OnTimeChangedListener, View.OnClickListener {

    override fun onClick(v: View?) {
        // TODO: PRESENT IMAGE PICKER / CAMERA
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        view?.let { timePicker ->
            val timePickerTag = timePicker.tag
            if (timePickerTag is Int) {
                when (timePickerTag) {
                    0 -> {
                        val dateAtCorrectTime = wateringDate!!.getDateAtDesiredTime(hourOfDay,minute)
                        textView_next_watering_reminder.text = dateAtCorrectTime.toString()
                    }
                    1 -> {
                        val dateAtCorrectTime = fertilizingDate!!.getDateAtDesiredTime(hourOfDay,minute)
                        textView_next_fertilizing.text = dateAtCorrectTime.toString()
                    }
                }
            }
        }
    }

    override fun onReminderUpdated(next: Date, interval: Int, isForFertilizing: Boolean) {
        setupReminderUi(next,interval,isForFertilizing)
    }

    override fun colorSelected(colorId: Int) {
        selectedColor = colorId
        println("Selected Color: $selectedColor")
    }

    private var name = ""
    // TODO: needs to be red -- FIX MAGIC NUMBER PROBLEM HERE FOR COLOR
    private var selectedColor = -1754827
    private var wateringDate: Date? = null
    private var wateringInterval: Int? = null
    private var fertilizingDate: Date? = null
    private var fertilizingInterval: Int? = null
    private var photoUri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateBottomToolbar()
        setupColorRecyclerView()
        setupReminderClickListeners()
        setupTimePickers()
        setupPhotoImageButton()
    }

    private fun inflateBottomToolbar() {
        toolbar_cancel_save.inflateMenu(R.menu.detail_toolbar)
    }

    private fun setupColorRecyclerView() {
        this.context?.let { context ->
            recyclerView_plant_colors.layoutManager = GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
            recyclerView_plant_colors.adapter = ColorsAdapter(context,this)
        }
    }

    private fun setupReminderClickListeners() {
        // TODO: DRY
        this.view?.let {
            imageButton_setup_watering.setOnClickListener {
                val setupDialog = PlantReminderSetupDialog(this,false)
                setupDialog.show(this.activity!!.supportFragmentManager,"reminder_dialog")
            }
            imageButton_setup_fertilizing.setOnClickListener{
                val setupDialog = PlantReminderSetupDialog(this,true)
                setupDialog.show(this.activity!!.supportFragmentManager,"reminder_dialog")
            }
        }
    }

    private fun setupTimePickers() {
        timePicker_watering_time.setOnTimeChangedListener(this)
        timePicker_watering_time.tag = 0
        timePicker_setup_fertilizing.setOnTimeChangedListener(this)
        timePicker_setup_fertilizing.tag = 1
    }

    private fun setupPhotoImageButton() {
        imageButton_plant_photo.setOnClickListener(this)
    }

    private fun setupReminderUi(nextDate: Date, interval: Int, isForFertilizing: Boolean) {
        val intervalText = "$interval Days"
        if (isForFertilizing) {
            fertilizingDate = nextDate
            fertilizingInterval = interval
            timePicker_setup_fertilizing.isVisible = true
            textView_next_fertilizing.text = nextDate.toString()
            textView_fertilizing_interval.text = intervalText
        } else {
            wateringDate = nextDate
            wateringInterval = interval
            timePicker_watering_time.isVisible = true
            textView_next_watering_reminder.text = nextDate.toString()
            textView_watering_interval.text = intervalText
        }
    }
}
