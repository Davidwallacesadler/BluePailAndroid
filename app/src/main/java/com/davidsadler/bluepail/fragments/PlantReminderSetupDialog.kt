package com.davidsadler.bluepail.fragments

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.util.toShortString
import com.davidsadler.bluepail.viewModels.PlantReminderViewModel
import com.squareup.timessquare.CalendarPickerView
import kotlinx.android.synthetic.main.fragment_plant_reminder_setup_dialog.*
import java.util.*

class PlantReminderSetupDialog internal constructor(private val onReminderUpdatedListener: OnReminderUpdatedListener, private val fertilizerSetup: Boolean) : DialogFragment(), View.OnClickListener, CalendarPickerView.OnDateSelectedListener  {

    private lateinit var viewModel: PlantReminderViewModel

    override fun onDateSelected(date: Date?) {
        date?.let { selectedDate ->
            if (viewModel.getFirstDate() == null) {
                viewModel.updateFirstDate(selectedDate)
                updateUiOnFirstDateSelected()
            } else {
                viewModel.updateSecondDate(selectedDate)
                updateUiOnSecondDateSelected()
            }
        }
    }

    override fun onDateUnselected(date: Date?) {
        if (viewModel.getFirstDate() != null) {
            viewModel.updateFirstDate(null)
            viewModel.updateSecondDate(null)
            textView_interval.text = getString(R.string.text_view_reminder_interval_placeholder)
        }
    }

    override fun onClick(v: View?) {
        if (allRequiredParametersAreSet()) {
            onReminderUpdatedListener.onReminderUpdated(viewModel.getFirstDate()!!,viewModel.getInterval()!!,fertilizerSetup)
            this.dismiss()
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
        setupCalendarGrid()
        changeColorsToFertilizerSchemeIfNeeded()
        setupDoneButtonClickListener()
        updateUiOnFirstDateSelected()
        updateUiOnSecondDateSelected()
    }

    override fun onResume() {
        super.onResume()
        val params:ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as android.view.WindowManager.LayoutParams
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

    private fun setupCalendarGrid() {
        val today = Date()
        val oneYearInTheFuture = Calendar.getInstance()
        oneYearInTheFuture.add(Calendar.YEAR, 1)
        calendarView_reminder_setup.init(today,oneYearInTheFuture.time).inMode(CalendarPickerView.SelectionMode.RANGE)
        calendarView_reminder_setup.setOnDateSelectedListener(this)
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

    private fun setupDoneButtonClickListener() {
        button_done.setOnClickListener(this)
    }

    private fun updateUiOnFirstDateSelected() {
        if (viewModel.getFirstDate() != null) {
            textView_firstDate.text = viewModel.getFirstDate()!!.toShortString(false)
            updateHintTextView(SetupStage.SECOND)
        }
    }

    private fun updateUiOnSecondDateSelected() {
        if (viewModel.getSecondDate() != null) {
            textView_interval.text = viewModel.getReadableInterval()
            updateHintTextView(SetupStage.DONE)
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
            return false
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