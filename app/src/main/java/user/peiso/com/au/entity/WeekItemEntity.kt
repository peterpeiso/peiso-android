package user.peiso.com.au.entity

data class WeekItemEntity(
    val dashBoardId: Int,
    val monDateTime: Int,
    val sunDateTime: Int,
    val value: String,
    val weekFlag: Int
)