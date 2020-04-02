package com.davidsadler.bluepail.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.davidsadler.bluepail.util.amountOfDaysToOtherDate
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
            when (val interval = firstDate!!.amountOfDaysToOtherDate(secondDate!!)) {
                1 -> "1 Day"
                else -> "$interval Days"
            }
        } else {
            null
        }
    }

    fun updateFirstDate(date: Date) {
        firstDate = date
    }

    fun updateSecondDate(date: Date) {
        secondDate = date
    }

    fun getDateFromYearMonthDay(year: Int,month: Int,dayOfMonth: Int) : Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar.time
    }
}