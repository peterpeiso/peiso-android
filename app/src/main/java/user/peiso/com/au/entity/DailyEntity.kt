package user.peiso.com.au.entity

data class DailyEntity(
    val actualPOJO: ActualPOJO,
    val daysPOJO: DaysPOJO,
    val targetPOJO: TargetPOJO,
    val venueId: Int,
    val venueName: String
){
    data class ActualPOJO(
        val actualRevenue: Double?,
        val labourPOJO: LabourPOJO?
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

    data class LabourPOJOX(
        val cost: Double,
        val hours: Double
    )

    data class TargetPOJO(
        val labourPOJO: LabourPOJOX?,
        val profitTarget: Double?,
        val revenueTarget: Double?,
        val rosterTarget: Double?,
        val rosterTargetPOJO: LabourPOJOX?
    )
}