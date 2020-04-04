package com.davidsadler.bluepail.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.davidsadler.bluepail.util.amountOfDaysToOtherDate
import com.davidsadler.bluepail.util.getDaysAwayFromAnotherDate
import java.util.*

class PlantReminderViewModel(application: Application): AndroidViewModel(application) {

    private var firstDate: Date? = null
    private var secondDate: Date? = null

    fun getFirstDate(): Date? {
        return firstDate
    }

    fun getSecondDate(): Date? {
        return secondDate
    }

    fun getInterval(): Int? {
        return if (firstDate != null && secondDate != null) {
            firstDate!!.amountOfDaysToOtherDate(secondDate!!)
        } else {
            null
        }
    }

    fun getReadableInterval(): String? {
        return if (firstDate != null && secondDate != null) {
            firstDate!!.getDaysAwayFromAnotherDate(secondDate!!,false)
        } else {
            null
        }
    }

    fun updateFirstDate(date: Date?) {
        firstDate = date
    }

    fun updateSecondDate(date: Date?) {
        secondDate = date
    }
}