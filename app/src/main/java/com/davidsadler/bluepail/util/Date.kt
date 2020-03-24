package com.davidsadler.bluepail.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

fun Date.getDaysAwayFromNow(includesTime: Boolean) : String {
    val calendar = Calendar.getInstance()
    val todaysDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.time = this
    val inputDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val daysBetweenDate = inputDayOfYear.minus(todaysDayOfYear)
    var returnString: String
    returnString = if (daysBetweenDate >= 0) {
        when (daysBetweenDate) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> "$daysBetweenDate Days"
        }
    } else {
        val absoluteValueDays = daysBetweenDate.toDouble().absoluteValue.toInt()
        when (daysBetweenDate) {
            -1 -> "Yesterday"
            else -> "$absoluteValueDays Days ago"
        }
    }
    if (includesTime) {
        val hourAndMinuteDateFormat = "hh:mm a"
        val simpleFormatter = SimpleDateFormat(hourAndMinuteDateFormat)
        val timeString = simpleFormatter.format(this)
        returnString = returnString.plus(" ($timeString)")
    }
    return returnString
}

fun Date.getDateAmountOfDaysAway(daysAway: Int, inputDate: Date = this): Date {
    val calendar = Calendar.getInstance()
    calendar.time = inputDate
    val todaysDayOfTheYear = calendar.get(Calendar.DAY_OF_YEAR)
    val targetDay = todaysDayOfTheYear.plus(daysAway)
    calendar.set(Calendar.DAY_OF_YEAR, targetDay)
    return calendar.time
}

fun Date.getDateAtDesiredTime(hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    return calendar.time
}

fun Date.getHour(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.HOUR)
}

fun Date.getMinute() : Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.MINUTE)
}

fun Date.amountOfDaysToOtherDate(otherDate: Date) : Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.time = otherDate
    val otherDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    return otherDayOfYear - currentDayOfYear
}

fun Date.timeInMillis() : Long {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.timeInMillis
}