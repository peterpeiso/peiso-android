package user.peiso.com.au.entity

data class TodaySummaryEntity(
    val actualPOJO: ActualPOJO,
    val appTodaySummaryShowFlag: Boolean?,
    val daysPOJO: DaysPOJO,
    val targetPOJO: TargetPOJO,
    val venueId: Int,
    val venueName: String,
    val appRefreshTimeMinutes: Int
)

data class DaysPOJO(
    val weekName: String,
    val weeks: Int,
    val years: Int
)
data class LabourPOJO(
    val cost: Double,
    val hours: Double
)

data class TargetPOJO(
    val labourPOJO: LabourPOJO,
    val profitTarget: Int,
    val revenueTarget: Int
)

data class ActualPOJO(
    val actualRevenue: Double,
    val labourPOJO: LabourPOJO
)