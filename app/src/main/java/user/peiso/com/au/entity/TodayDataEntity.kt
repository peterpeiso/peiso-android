package user.peiso.com.au.entity

data class TodayDataEntity(
    val actualPOJO: ActualPOJO?,
    val daysPOJO: DaysPOJO,
    val targetPOJO: TargetPOJO?,
    val venueId: Double,
    val venueName: String,
    val zoneId: String
){

    data class ActualRevenueHoursCostPOJO(
        val bl: Any,
        val cost: Double?,
        val groupNo: Any,
        val hours: Double?,
        val seqNo: Int?
    )

    data class ActualPOJO(
        val actualLabourHoursCostPOJOList: List<ActualRevenueHoursCostPOJO>,
        val actualRevenue: Double,
        val actualRevenueHoursCostPOJOList: List<ActualRevenueHoursCostPOJO>,
        val labourPOJO: LabourPOJO?
    )

    data class DaysPOJO(
        val rq: String,
        val weekIndex: Int,
        val weekName: String,
        val weeks: Int,
        val years: Int
    )

    data class LabourPOJO(
        val cost: Double,
        val hours: Double
    )

    data class RevenueTargetHoursCostPOJO(
        val bl: Any,
        val cost: Double?,
        val groupNo: Any,
        val hours: Double?,
        val seqNo: Int?
    )

    data class RosterTargetPOJO(
        val cost: Double,
        val hours: Double
    )

    data class TargetPOJO(
        val labourPOJO: LabourPOJO?,
        val labourTargetHoursCostPOJOList: List<RevenueTargetHoursCostPOJO>?,
        val profitTarget: Double,
        val revenueTarget: Double,
        val revenueTargetHoursCostPOJOList: List<RevenueTargetHoursCostPOJO>?,
        val rosterTarget: Double,
        val rosterTargetPOJO: RosterTargetPOJO?
    )
}