package i.library.base.utils

import android.text.TextUtils
import i.library.base.expands.logo
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by hc. on 2020/4/15
 * Describe: 时间
 */

const val TIME_UNIT_SIZE = 1000L

const val TIME_UNIT_DAY_MILLIS = 24 * 60 * 60 * 1000L

private val calendar = Calendar.getInstance()

fun timeGetLocal(): Locale{
    calendar.timeZone = TimeZone.getDefault()
    return Locale.getDefault()
}

/** 获取当前时间 **/
fun timeToFormatCurrent(format : String = "yyyy/MM/dd") : String{
    return timeToFormat(System.currentTimeMillis(),format)
}

fun timeToFormatDay(time: Long = System.currentTimeMillis()): Long{
    val sdf = SimpleDateFormat("yyyy/MM/dd", timeGetLocal())
    val date = Date()
    date.time = time
    val format = sdf.format(date)
    return try {
        val parse = sdf.parse(format)
        parse?.time?:0
    }catch (e : Exception){
        0
    }
}

/** 时间戳转格式2.0 **/
fun Long.timeToFormat2() : String{
    val minute = (this % (60 * 60)) / 60
    val second = (this % (60 * 60)) % 60
    return StringBuffer().apply {
        append(paddingNumber(minute))
        append(": ")
        append(paddingNumber(second))
    }.toString()
}

/* 补0 */
fun paddingNumber(i : Long) : String{
    return if(i < 10){
        "0$i"
    }else{
        "$i"
    }
}

fun Long.timeToFormatX(format : String = "yyyy/MM/dd"): String{
    return timeToFormat(this,format)
}

/** 时间戳转格式 **/
fun timeToFormat(long: Long,format : String = "yyyy/MM/dd") : String{
    val sdf = SimpleDateFormat(format, timeGetLocal())
    val date = Date()
    date.time = long
    var formatX = sdf.format(date)
    if(format.contains("a")){
        formatX = formatX.replace("am","AM").replace("pm","PM")
    }
    if(formatX.contains(".")){
        formatX = formatX.replace(".","")
    }
    return formatX
}

/** 周 -> 缩写 **/
fun weekToAbbreviation(i: Int) : String{
    return when(i){
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "*"
    }
}

/** 是否是今天 **/
fun isToday(time: Long): Boolean {
    val timeToFormat = timeToFormat(time)
    val currentFormat = timeToFormatCurrent()
    return timeToFormat == currentFormat
}

/** 是否明天 **/
fun isTomorrow(time: Long): Boolean {
    val timeToFormat = timeToFormat(System.currentTimeMillis() + (1000 * 60 * 60 * 24))
    val formatToTime = formatToTime("yyyy/MM/dd", timeToFormat)
    return time >= formatToTime
}

/** 时间戳转数组 **/
fun timeToIntArray(str : String,mark : String) : List<Int>{
    val array = ArrayList<Int>()
    val split = str.split(mark)
    for(i in split){
        array.add(AMathUtils.toConvertInt(i))
    }
    return array
}

/** 指定格式转时间戳 **/
fun formatToTime(format : String,date : String?,isNullUseCurrent: Boolean = false) : Long{
    if(date == null || TextUtils.isEmpty(date)){
        return if(isNullUseCurrent) System.currentTimeMillis() else 0
    }
    return try {
        val sdf = SimpleDateFormat(format, timeGetLocal())
        val parse = sdf.parse(date)
        parse?.time?:0
    }catch (e : Exception){
        0
    }
}

fun iGetDay():Long{
    return 1000 * 60 * 60 * 24
}

fun formatMonth(i: Int): String{
    return when(i){
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }
}

fun formatMonthX(i: Int): String{
    return when(i){
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> ""
    }
}
