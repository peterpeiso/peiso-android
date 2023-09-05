package user.peiso.com.au.ui.chart

import androidx.databinding.ObservableField
import i.library.base.base.BaseViewModel
import user.peiso.com.au.entity.WeekItemEntity

/**
 * Created by forever_zero on 2023/1/6.
 **/
class UiChartVModel: BaseViewModel() {

    var iWeekList: List<WeekItemEntity> ?= null

    val iWeek = ObservableField("THIS WEEK")

    /* lastWeek=-1 thisWeek=0 nextWeek=1 未来都是>1 */
    var iWeekType = 0

    fun iGetWeek(): WeekItemEntity?{
        val list = iWeekList?:return null
        for (i in list){
            if(i.weekFlag == iWeekType){
                return i
            }
        }
        return null
    }
}