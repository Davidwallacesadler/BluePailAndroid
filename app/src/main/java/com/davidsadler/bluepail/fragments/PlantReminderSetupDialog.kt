package com.davidsadler.bluepail.fragments

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.util.amountOfDaysToOtherDate
import com.davidsadler.bluepail.viewModels.PlantReminderViewModel
import kotlinx.android.synthetic.main.fragment_plant_reminder_setup_dialog.*
import java.util.*

class PlantReminderSetupDialog internal constructor(private val onReminderUpdatedListener: OnReminderUpdatedListener, private val fertilizerSetup: Boolean) : DialogFragment(), CalendarView.OnDateChangeListener, View.OnClickListener {

    private lateinit var viewModel: PlantReminderViewModel

    override fun onClick(v: View?) {
        if (allRequiredParametersAreSet()) {
            onReminderUpdatedListener.onReminderUpdated(viewModel.getFirstDate()!!,viewModel.getInterval()!!,fertilizerSetup)
            this.dismiss()
        }
    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = viewModel.getDateFromYearMonthDay(year,month,dayOfMonth)
        if (selectedDate >= Date()) {
            if (viewModel.getFirstDate() == null) {
                viewModel.updateFirstDate(selectedDate)
                updateUiOnFirstDateSelected()
            } else {
                val differenceInDays = viewModel.getFirstDate()!!.amountOfDaysToOtherDate(selectedDate)
                if (differenceInDays <= 0) {
                    showToastWithText(getString(R.string.toast_reminder_setup_select_future_date))
                    calendarView_reminder_setup.date = viewModel.getFirstDate()!!.time
                } else {
                    viewModel.updateSecondDate(selectedDate)
                    updateUiOnSecondDateSelected()
                }
            }
        } else {
            showToastWithText(getString(R.string.toast_reminder_setup_select_today))
            calendarView_reminder_setup.date = Date().time
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupViewModel()
        return inflater.inflate(R.layout.fragment_plant_reminder_setup_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeColorsToFertilizerSchemeIfNeeded()
        setupCalendarClickListener()
        setupDoneButtonClickListener()
        updateUiBasedOnProgress()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        retainInstance = true
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlantReminderViewModel::class.java)
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

    private fun setupCalendarClickListener() {
        calendarView_reminder_setup.setOnDateChangeListener(this)
    }

    private fun setupDoneButtonClickListener() {
        button_done.setOnClickListener(this)
    }

    private fun updateUiOnFirstDateSelected() {
        if (viewModel.getFirstDate() != null) {
            textView_firstDate.text = viewModel.getFirstDate().toString()
            updateHintTextView(SetupStage.SECOND)
        }
    }

    private fun updateUiOnSecondDateSelected() {
        if (viewModel.getSecondDate() != null) {
            textView_interval.text = viewModel.getReadableInterval()
            updateHintTextView(SetupStage.DONE)
        }
    }

    private fun updateUiBasedOnProgress() {
        if (viewModel.getFirstDate() != null) {
            textView_firstDate.text = viewModel.getFirstDate().toString()
            updateHintTextView(SetupStage.SECOND)
            calendarView_reminder_setup.date = viewModel.getFirstDate()!!.time
        }
        if (viewModel.getSecondDate() != null) {
            textView_interval.text = viewModel.getReadableInterval()
            updateHintTextView(SetupStage.DONE)
            calendarView_reminder_setup.date = viewModel.getSecondDate()!!.time
        }
    }

    private fun updateHintTextView(currentSetupStage: SetupStage) {
        textView_reminder_setup_hint.text = when (currentSetupStage) {
            SetupStage.FIRST -> getString(R.string.text_view_reminder_setup_step_1)
            SetupStage.SECOND -> getString(R.string.text_view_reminder_setup_step_2)
            SetupStage.DONE -> getString(R.string.text_view_reminder_setup_step_3)
        }
    }

    private fun allRequiredParametersAreSet(): Boolean {
        if (viewModel.getFirstDate() == null) {
            showToastWithText("Please select the first date")
            return false
        }
        if (viewModel.getSecondDate() == null) {
            showToastWithText("Please select the second date")
        }
        return true
    }

    private fun showToastWithText(text: String) {
        Toast.makeText(this.activity,text, Toast.LENGTH_SHORT).show()
    }
}

interface OnReminderUpdatedListener {
    fun onReminderUpdated(next: Date, interval: Int, isForFertilizing: Boolean)
}

enum class SetupStage {
    FIRST,SECOND,DONE
}