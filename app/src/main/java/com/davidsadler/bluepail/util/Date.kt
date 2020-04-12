package com.davidsadler.bluepail.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

fun Date.getDaysAwayFromNow(includesTime: Boolean): String {
    val calendar = Calendar.getInstance()
    val todaysDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.time = this
    val inputDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val daysBetweenDate = inputDayOfYear.minus(todaysDayOfYear)
    var returnString: String
    val weekCount = daysBetweenDate.div(7)
    if (weekCount > 0) {
        val daysIntoCurrentWeek = daysBetweenDate.rem(7)
        returnString = when (weekCount) {
            1 -> "1 Week"
            else -> "$weekCount Weeks"
        }
        if (daysBetweenDate > 0) {
            returnString = returnString.plus(when (daysBetweenDate) {1 -> ", 1 Day" else -> ", $daysIntoCurrentWeek Days" })
        }
    } else {
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
            val simpleFormatter = SimpleDateFormat(hourAndMinuteDateFormat, Locale.US)
            val timeString = simpleFormatter.format(this)
            returnString = returnString.plus(" ($timeString)")
        }
    }
    return returnString
}

fun Date.getDaysAwayFromAnotherDate(otherDate: Date, includesTime: Boolean): String {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val inputDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.time = otherDate
    val otherDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val daysBetweenDate = otherDayOfYear.minus(inputDayOfYear)
    var returnString: String
    val weekCount = daysBetweenDate.div(7)
    if (weekCount > 0) {
        val daysIntoCurrentWeek = daysBetweenDate.rem(7)
        returnString = when (weekCount) {
            1 -> "1 Week"
            else -> "$weekCount Weeks"
        }
        if (daysIntoCurrentWeek > 0) {
            returnString = returnString.plus(when (daysBetweenDate) {1 -> ", 1 Day" else -> ", $daysIntoCurrentWeek Days" })
        }
    } else {
        returnString = if (daysBetweenDate >= 0) {
            when (daysBetweenDate) {
                0 -> "Today"
                1 -> "1 Day"
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
            val simpleFormatter = SimpleDateFormat(hourAndMinuteDateFormat, Locale.US)
            val timeString = simpleFormatter.format(this)
            returnString = returnString.plus(" ($timeString)")
        }
    }
    return returnString
}

fun Date.getDateAmountOfDaysAway(daysAway: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
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

fun Date.getHour(modTwelve: Boolean): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return if (modTwelve) {
        calendar.get(Calendar.HOUR)
    } else {
        calendar.get(Calendar.HOUR_OF_DAY)
    }
}

fun Date.getMinute(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.MINUTE)
}

fun Date.amountOfDaysToOtherDate(otherDate: Date): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.time = otherDate
    val otherDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    return otherDayOfYear - currentDayOfYear
}

fun Date.timeInMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.timeInMillis
}

fun Date.toShortString(includesTime: Boolean): String {
    var baseFormat = "EEE, MMM d, yyyy"
    if (includesTime) {
        baseFormat = baseFormat.plus(" (hh:mm a)")
    }
    val simpleFormatter = SimpleDateFormat(baseFormat, Locale.US)
    return simpleFormatter.format(this)
}