package i.library.base.utils

import android.text.TextUtils
import com.google.gson.Gson
import java.math.BigDecimal
import java.text.DecimalFormat
import com.google.gson.reflect.TypeToken
import i.library.base.expands.logo
import i.library.base.utils.AMathUtils.formatHours
import kotlin.math.abs


/**
 * Created by hc. on 2020/6/15
 * Describe:
 */
object AMathUtils {

    fun Double?.formatPrice(isUsePM: Boolean = true): String {
        val zero = this ?: 0.0
        return "${if(zero >= 0) (if(isUsePM) "+" else "") else "-"}$${formatSize(abs(zero))}"
    }

    fun Double?.formatHours(isUsePM: Boolean = true): String {
        val zero = this ?: 0.0
        return "${if(zero >= 0) (if(isUsePM) "+" else "") else "-"}${formatSize(abs(zero), scale = 1, isUseZero = false)} hrs"
    }

    inline fun <reified T> List<*>.listGsonClone() : List<T>{
        val toJson = Gson().toJson(this)
        return Gson().fromJson(toJson, object : TypeToken<List<T>>() {}.type)
    }

    fun Double.formatBigDecimal(): BigDecimal{
        return BigDecimal(this)
    }

    fun iGetStartNumber(value: String?): Double{
        if(value == null || value.isEmpty()){
            return 0.0
        }
        val number = StringBuffer()
        for (i in value.indices){
            val it = value[i]
            when{
                "$it".matches(Regex("\\d+")) -> {
                    number.append(it)
                }
                it == '.' && !number.contains('.')-> {
                    number.append(it)
                }
                else -> {
                    break
                }
            }
        }
        return toConvertDouble(number.toString())
    }

    /** 转换成价格格式 **/
    fun Double?.toPrice(unit: String = "$") : String{
        return if(this != null) {
            "".logo()
            formatSize(this,unit)
        } else {
            "${unit}0.00"
        }
    }

    fun toValuePrice(value : Double) : String{
        return formatSize(value,"$")
    }

    /** 大小格式化 **/
    fun formatSize(value : Double,
                           unit: String = "",
                           scale: Int = 2,
                           isUseZero: Boolean = true) : String{
        val isLessZero = value < 0
        val valueStr = formatDoubleStr(abs(value),scale)
        val strBuffer = StringBuffer()
        val split = valueStr.split(".")
        /* 整数 */
        val iX = if(split.size <= 1){
            valueStr
        }else{
            split[0]
        }
        /** 整数加， **/
        val max = if(iX.isNotEmpty()) iX.length - 1 else 0
        if(max != 0){
            for(i in max downTo 0){
                strBuffer.append(iX[max -i])
                if(i != 0 && i % 3 == 0){
                    strBuffer.append(',')
                }
            }
        }else{
            strBuffer.append(iX)
        }
        val unitZ = if(isLessZero) "-$unit" else unit
        return if(split.size == 2) {
            if(!isUseZero){
                val later = split[1]
                when{
                    later == "00" -> "$unitZ${strBuffer}"
                    later.endsWith("0") && split[1].length == 2 -> "$unitZ${strBuffer}.${split[1].substring(0,1)}"
                    later.endsWith("0") && split[1].length == 1 -> "$unitZ${strBuffer}"
                    else -> "$unitZ${strBuffer}.${split[1]}"
                }
            }else{
                "$unitZ${strBuffer}.${split[1]}"
            }
        }else{
            "$unitZ${strBuffer}"
        }
    }

    /** 转价格格式 **/
    fun toConvertPrice(str : String) : String{
        val scale = BigDecimal(str).setScale(2, BigDecimal.ROUND_HALF_UP)
        return "$$scale"
    }

    fun String?.toConvertIntX(): Int{
        if(this == null
            || TextUtils.isEmpty(this)
            || !this.matches(Regex("\\d+"))
            || this.length > 11){
            return 0
        }
        return this.toInt()
    }

    /** 转Int **/
    fun toConvertInt(str : String?) : Int{
        if(str == null
            || TextUtils.isEmpty(str)
            || !str.matches(Regex("\\d+"))
            || str.length > 11){
            return 0
        }
        return str.toInt()
    }

    /** 转Int **/
    fun toConvertLong(str : String?) : Long{
        if(str == null || TextUtils.isEmpty(str) || !str.matches(Regex("\\d+"))){
            return 0
        }
        return str.toLong()
    }

    /** 转Double **/
    fun toConvertDouble(str : String?) : Double{
        if (str == null || str.isEmpty()) {
            return 0.0
        }
        return try {
            str.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun String?.formatDouble(errorValue: Double = 0.0,type: String = "price"): Double{
        if(this == null || TextUtils.isEmpty(this)){
            return errorValue
        }
        val value = when {
            TextUtils.isEmpty(type) -> {
                this
            }
            type == "price" -> {
                this.replace("$","").replace(",","")
            }
            else -> {
                "$errorValue"
            }
        }
        return try {
            value.toDouble()
        }catch (e: java.lang.Exception){
            errorValue
        }
    }

    /** 转金额格式 - 保留两位小数 **/
    fun formatPrice(double: Double?) : String{
        if(double == null){
            return "0.00"
        }
        return double.toPrice()
    }


    /** 保留小数 四舍五入 **/
    fun formatDouble(double: Double?, scale : Int = 2) : Double{
        if(double == null){
            return 0.00
        }
        val df = BigDecimal(double).setScale(scale,BigDecimal.ROUND_HALF_UP)
        return df.toDouble()
    }

    /** 保留小数 **/
    fun formatDoubleNot(double: Double?, format : String = "#.00") : Double{
        if(double == null){
            return 0.00
        }
        val df = DecimalFormat(format)
        return df.format(double).toDouble()
    }

    /** 保留小数  - 最少两位 - 四舍五入 **/
    fun formatDoubleStr(double: Double?, scale : Int = 2) : String{
        if(double == null){
            return "0.00"
        }
        val df = BigDecimal(double).setScale(scale,BigDecimal.ROUND_HALF_UP)
        val toDouble = df.toString()
        val split = toDouble.split(".")
        return if(split.size == 2){
            if(split[1].length >= scale ){
                toDouble
            }else{
                var k = ""
                for(i in 0..scale - split[1].length){
                    k += "0"
                }
                toDouble + k
            }
        }else{
            toDouble
        }
    }

    /** 大小格式化 **/
    fun formatAreaSize(size : Double) : String{
        val d1 = size / 1000.0
        val value = if(d1 > 1){
            val scale = BigDecimal(d1).setScale(0, BigDecimal.ROUND_HALF_UP)
            "${scale.toInt() * 1000}"
        }else{
            formatDoubleStr(d1 * 1000,0)
        }
        val strBuffer = StringBuffer()
        val split = value.split(".")
        /* 整数 */
        val iX =  if(split.size <= 1){
            value
        }else{
            split[0]
        }
        /** 整数加， **/
        val max = if(iX.isNotEmpty()) iX.length - 1 else 0
        if(max == 0){
            val toConvertDouble = toConvertDouble(value)
            return formatDouble(toConvertDouble, 0).toInt().toString()
        }
        for(i in max downTo 0){
            strBuffer.append(iX[max -i])
            if(i != 0 && i % 3 == 0){
                strBuffer.append(',')
            }
        }
        return strBuffer.toString()
    }

    fun Double.formatDouble(): String{
        val str = formatDoubleStr(this)
        val i = str.split(".")
        if(i.size != 2) return "$this"
        return when{
            i[1] == "00" -> {
                "${toInt()}"
            }
            i[1].endsWith("0") -> {
                i[1].substring(0,i[1].length - 1)
            }
            else -> {
                str
            }
        }
    }

    /* ID */
    fun createFakeID(str : String) : Long{
        if(str.length > 500){
            return System.currentTimeMillis()
        }
        var allSize = str.length
        val strBuffer = StringBuffer()
        for(i in str.indices){
            val toInt = str[i].code
            allSize += (toInt * i)
            strBuffer.append(toInt)
        }
        return when{
            strBuffer.length <= 12 -> {
                strBuffer.toString().toLong()
            }
            else -> {
                StringBuffer()
                    .append(allSize)
                    .append(strBuffer.substring(strBuffer.length - (18 - "$allSize".length),strBuffer.length))
                    .toString().toLong()
            }
        }
    }

    fun createIntFakeID(str: String): Int{
        var allSize = str.length
        val strBuffer = StringBuffer()
        for(i in str.indices){
            val toInt = str[i].code
            allSize += (toInt * i)
            strBuffer.append(toInt)
        }
        return if(strBuffer.length <= 9){
            strBuffer.toString().toInt()
        }else{
            StringBuffer()
                .append(allSize)
                .append(strBuffer.substring(strBuffer.length - (9 - "$allSize".length),strBuffer.length))
                .toString()
                .toInt()
        }
    }

    fun String.formatMobile(): String{
        return when{
            length == 9 && startsWith("4") -> {
                "0${substring(0,3)} ${substring(3,6)} ${substring(6,length)}"
            }
            length == 10 && startsWith("04") -> {
                "${substring(0,4)} ${substring(4,7)} ${substring(7,length)}"
            }
            else -> {
                this
            }
        }
    }

    fun String.formatCard(): String{
        var start = 0
        var end = 4
        val builder = StringBuilder()
        while (start < length){
            if(start != 0)builder.append(" ")
            builder.append(substring(start,end))
            start = end
            end = if(end + 4 > length) length else end + 4
        }
        return builder.toString()
    }

    fun String.formatBSB(): String{
        var start = 0
        var end = 2
        val builder = StringBuilder()
        while (start < length){
            if(start != 0)builder.append("-")
            builder.append(substring(start,end))
            start = end
            end = if(end + 2 > length) length else end + 2
        }
        return builder.toString()
    }
}