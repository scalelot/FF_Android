package com.festum.festumfield.verstion.firstmodule.utils

import android.content.Context
import com.festum.festumfield.R
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    const val FORMAT_API_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    var newLocale = Locale("en")

    fun getNowSeconds() = System.currentTimeMillis() / 1000L

    fun addStringTimeToDate(date: Date?): Date {
//        val serverTime = SimpleDateFormat("HH:mm:ss", newLocale).parse(time)
        val calendarTime = Calendar.getInstance()
//        calendarTime.time = serverTime
        val calendarDate = Calendar.getInstance()
        calendarDate.time = date
        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
        calendarDate.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND))
        return calendarDate.time
    }

    fun getChatDate(neededTimeMilis: Long, context: Context, needsToday: Boolean): String {

        val nowTime = Calendar.getInstance()
        val neededTime = Calendar.getInstance()
        neededTime.timeInMillis = neededTimeMilis

        return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
            if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
                if (neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1 && needsToday) {
                    //here return like "Tomorrow at 12:00"
                    context.resources.getString(R.string.tomorrow)
                } else if (nowTime[Calendar.DATE] == neededTime[Calendar.DATE] && needsToday) {
                    //here return like "Today at 12:00"
                    context.resources.getString(R.string.today)
                } else if (nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 && needsToday) {
                    //here return like "Yesterday at 12:00"
                    context.resources.getString(R.string.yesterday)
                } else {
                    //here return like "31 May 2023"
                    SimpleDateFormat(
                        "dd MMMM yyyy", newLocale
                    ).format(
                        Date(neededTime.timeInMillis)
                    ).toString()
//                    context.resources.getString(R.string.this_month)
                }
            } else {
                //here return like "May 31, 12:00"
                SimpleDateFormat(
                    "MMMM", newLocale
                ).format(
                    Date(neededTimeMilis)
                ).toString()
            }
        } else {
            //here return like "May 31 2010, 12:00" - it's a different year we need to show it
            SimpleDateFormat(
                "MMMM yyyy", newLocale
            ).format(
                Date(neededTimeMilis)
            ).toString()
        }
    }

}