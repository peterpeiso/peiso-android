package user.peiso.com.au.entity

data class InformationEntity(
    val mobileNumber: String,
    val userId: String,
    val venueUserInfoList: List<VenueUserInfo>?,
    var venue: VenueUserInfo?
){

    fun iGetVenue(): VenueUserInfo?{
        return if(venueUserInfoList?.isNotEmpty() == true) venueUserInfoList[0] else null
    }

    data class VenueUserInfo(
        val fullName: String,
        val id: Int,
        val mobileNumber: String,
        val userId: String,
        val venueId: Int,
        val venueName: String
    ){
        fun iGetVenueID(): String{
            return "$venueId"
        }
    }
}
