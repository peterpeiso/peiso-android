package user.peiso.com.au.entity

import android.text.TextUtils
import androidx.compose.ui.unit.TextUnit
import i.library.base.utils.AMathUtils
import i.library.base.utils.AMathUtils.formatHours
import i.library.base.utils.AMathUtils.formatPrice
import kotlin.math.abs

/**
 * Created by forever_zero on 2023/1/11.
 **/
data class GuidanceUIEntity(
    val actualPOJO: ActualPOJO,
    val daysPOJO: DaysPOJO,
    val labourHoursDeviation: Double,
    val labourHoursGuidance: Double,
    val labourMessage: String,
    val revenueDeviation: Double,
    val revenueMessage: String,
    val targetPOJO: TargetPOJO,
    val todayCloseFlag: Boolean,
    val venueId: Int,
    val venueName: String,
    val zoneId: String,
    val revenueGuidance: Double
){
    data class DaysPOJO(
        val weekName: String,
        val weeks: Int,
        val years: Int
    )

    data class ActualPOJO(
        val actualRevenue: Double,
        val labourPOJO: LabourPOJO
    )

    data class LabourPOJO(
        val cost: Double,
        val hours: Double
    )

    data class TargetPOJO(
        val labourPOJO: LabourPOJO,
        val profitTarget: Any,
        val revenueTarget: Double,
        val rosterTarget: Any
    )

    fun iGetGuidance(isLabour: Boolean): String{
        return when{
            isLabour && TextUtils.isEmpty(labourMessage) ->{
                labourHoursGuidance.formatHours(false)
            }
            isLabour -> {
                labourMessage
            }
            TextUtils.isEmpty(revenueMessage) ->{
                revenueGuidance.formatPrice(false)
            }
            else -> revenueMessage
        }
    }

    fun isComplete(isLabour: Boolean): Boolean{
        return if(isLabour) !TextUtils.isEmpty(labourMessage) else !TextUtils.isEmpty(revenueMessage)
    }

    fun iGetRevenue(): String{
        return revenueDeviation.formatPrice()
    }

    fun iGetLabour(): String{
        return labourHoursDeviation.formatHours()
    }
}