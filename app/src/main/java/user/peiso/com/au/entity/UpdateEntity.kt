package user.peiso.com.au.entity

data class UpdateEntity(
    val isForce: Boolean?,
    val isForceAndroid: Boolean?,
    val minVersion: String,
    val type: Int,
    val updateDetails: String,
    val updateTime: Int,
    val version: String
)