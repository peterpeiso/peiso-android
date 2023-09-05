package user.peiso.com.au.enums

/**
 * Created by forever_zero on 2023/1/13.
 **/

enum class GuidanceTypeEnum(val id: Int,val value: String,val title: String,val complete: String){
    Revenue(0,"Revenue", "Increase revenue by",
        "Well Done,\nYour Revenue is on Target"),
    Labour(1,"Labour", "Reduce labour force by",
        "Well Done, \nYour Labour is under Budget")
}