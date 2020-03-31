package com.davidsadler.bluepail.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.util.amountOfDaysToOtherDate
import kotlinx.android.synthetic.main.fragment_plant_reminder_setup_dialog.*
import java.util.*

class PlantReminderSetupDialog internal constructor(private val onReminderUpdatedListener: OnReminderUpdatedListener, private val fertilizerSetup: Boolean) : DialogFragment(), CalendarView.OnDateChangeListener {

    private var firstDateSelected = false
    private var nextReminderDate = Date()
    private var interval = 1

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = getDateFromYearMonthDay(year,month,dayOfMonth)
        if (selectedDate >= Date()) {
            if (!firstDateSelected) {
                nextReminderDate = selectedDate
                textView_firstDate.text = nextReminderDate.toString()
                firstDateSelected = !firstDateSelected
                updateHintTextView(SetupStage.INTERVAL)
            } else {
                val differenceInDays = nextReminderDate.amountOfDaysToOtherDate(selectedDate)
                if (differenceInDays <= 0) {
                    Toast.makeText(this.activity,getString(R.string.toast_reminder_setup_select_future_date), Toast.LENGTH_SHORT).show()
                    calendarView_reminder_setup.date = nextReminderDate.time
                } else {
                    val intervalText = when (differenceInDays) {
                        1 -> "$differenceInDays Day"
                        else -> "$differenceInDays Days"
                    }
                    textView_secondDate.text = intervalText
                    interval = differenceInDays
                    firstDateSelected = !firstDateSelected
                    updateHintTextView(SetupStage.DONE)
                }
            }
        } else {
            Toast.makeText(this.activity,getString(R.string.toast_reminder_setup_select_today), Toast.LENGTH_SHORT).show()
            calendarView_reminder_setup.date = Date().time
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plant_reminder_setup_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeColorsToFertilizerSchemeIfNeeded()
        calendarView_reminder_setup.setOnDateChangeListener(this)
        button_done.setOnClickListener {
            onReminderUpdatedListener.onReminderUpdated(nextReminderDate,interval,fertilizerSetup)
            this.dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return super.onCreateDialog(savedInstanceState)
    }

    private fun changeColorsToFertilizerSchemeIfNeeded() {
        if (fertilizerSetup) {
            this.context?.let {
                val fertilizerGreenColor = ContextCompat.getColor(it,R.color.fertilizerGreen)
                button_done.setBackgroundColor(fertilizerGreenColor)
                textView_selected_date_header.setTextColor(fertilizerGreenColor)
                textView_selected_date_header.text = getString(R.string.text_view_fertilizer_date_header)
                textView_fertilizing_interval.setTextColor(fertilizerGreenColor)
                textView_fertilizing_interval.text = getString(R.string.text_view_fertilizer_interval_header)
            }
        }
    }

    private fun getDateFromYearMonthDay(year: Int,month: Int,dayOfMonth: Int) : Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar.time
    }

    private fun updateHintTextView(currentSetupStage: SetupStage) {
        textView_reminder_setup_hint.text = when (currentSetupStage) {
            SetupStage.NEXT -> getString(R.string.text_view_reminder_setup_step_1)
            SetupStage.INTERVAL -> getString(R.string.text_view_reminder_setup_step_2)
            SetupStage.DONE -> getString(R.string.text_view_reminder_setup_step_3)
        }
    }
}

interface OnReminderUpdatedListener {
    fun onReminderUpdated(next: Date, interval: Int, isForFertilizing: Boolean)
}

enum class SetupStage {
    NEXT,INTERVAL,DONE
}