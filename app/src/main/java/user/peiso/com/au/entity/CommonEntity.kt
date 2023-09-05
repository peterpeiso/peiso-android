package user.peiso.com.au.entity

import i.library.base.utils.AMathUtils.formatHours
import i.library.base.utils.AMathUtils.formatPrice
import user.peiso.com.au.view.LineChartView

/**
 * Created by forever_zero on 2022/12/23.
 **/
data class TESTLineEntity(val color: Int, val name: String, val list: List<TESTLineNoteEntity>)

data class TESTLineNoteEntity(var value: Double?,var isUseHour: Boolean = false)
    : LineChartView.LineNoteInterface{

    override fun iGetRealValue(): Double {
        return value?:0.0
    }

    override fun iGetValue(): Double {
        return value?:-1.0
    }

    override fun iGetValueStr(): String {
        return if(isUseHour) "${iGetValue().formatHours(false)} "
        else iGetValue().formatPrice(false)
    }

}