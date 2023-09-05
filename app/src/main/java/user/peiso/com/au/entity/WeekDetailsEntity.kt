package user.peiso.com.au.entity

data class WeekDetailsEntity(
    val actualPOJO: ActualPOJO,
    val daysPOJO: DaysPOJO,
    val targetPOJO: TargetPOJO,
    val todayCloseFlag: Boolean,
    val venueId: Int,
    val venueName: String,
    val zoneId: String
){

    data class ActualPOJO(
        val actualRevenue: WeekItemValueEntity?,
        val labourPOJO: LabourPOJO?
    )

//    data class ActualRevenue(
//        val fri: Double,
//        val mon: Double,
//        val sat: Double,
//        val sun: Double,
//        val thu: Double,
//        val total: Double,
//        val tue: Double,
//        val wed: Double
//    )

//    data class Cost(
//        val fri: Double,
//        val mon: Double,
//        val sat: Double,
//        val sun: Double,
//        val thu: Double,
//        val total: Double,
//        val tue: Double,
//        val wed: Double
//    )

    data class DaysPOJO(
        val weekName: String,
        val weeks: Int,
        val years: Int
    )

//    data class Hours(
//        val fri: Double,
//        val mon: Double,
//        val sat: Double,
//        val sun: Double,
//        val thu: Double,
//        val tue: Double,
//        val wed: Double,
//        val total: Double
//    )

    data class LabourPOJO(
        val cost: WeekItemValueEntity,
        val hours: WeekItemValueEntity
    )

    data class LabourPOJOX(
        val cost: WeekItemValueEntity,
        val hours: WeekItemValueEntity
    )

    data class WeekItemValueEntity(
        val fri: Double?,
        val mon: Double?,
        val sat: Double?,
        val sun: Double?,
        val thu: Double?,
        val total: Double?,
        val tue: Double?,
        val wed: Double?
    )

    data class TargetPOJO(
        val labourPOJO: LabourPOJOX?,
        val rosterTargetPOJO: LabourPOJOX?,
        val profitTarget: WeekItemValueEntity?,
        val revenueTarget: WeekItemValueEntity?,
        val WeekItemValueEntity: WeekItemValueEntity?
    )
}