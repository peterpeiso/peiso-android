package user.peiso.com.au.ui.home.revenue

import android.graphics.Color
import android.view.View
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.listener.ClickInformBack
import i.library.base.utils.AMathUtils.formatPrice
import i.library.base.utils.AnimationManager
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiHomeRevenueBinding
import user.peiso.com.au.entity.TESTLineEntity
import user.peiso.com.au.entity.TESTLineNoteEntity
import user.peiso.com.au.entity.TodayDataEntity
import user.peiso.com.au.entity.WeekDetailsEntity
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.httpGetDaily
import user.peiso.com.au.net.httpGetTodayAllDataMinutes
import user.peiso.com.au.net.httpGetWeek

/**
 * Created by forever_zero on 2022/12/28.
 **/
class UiHomeRevenueFragment : BaseFragment<UiHomeRevenueBinding,UiHomeRevenueVModel>(),
    ClickInformBack {

    private var venueID = login.iGetVenueID()

    override val layoutID: Int
        get() = R.layout.ui_home_revenue

    override val mViewModel: UiHomeRevenueVModel
        get() = ViewModelProvider(this)[UiHomeRevenueVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
        binding.iTextDaily.isSelected = true
        binding.iTextWeekly.isSelected = false
        binding.iRefreshRevenue.setOnRefreshListener {
            refresh()
        }
        login.venue.observe(this){
            it?:return@observe
            binding.iRefreshRevenue.isRefreshing = true
            venueID = login.iGetVenueID()
            refresh()
        }
        binding.iChartView.chooses = {
            binding.iTextChartTargetValue.text = when{
                it.isEmpty() || it[0].iGetValue() == -1.0 -> "-"
                else -> it[0].iGetRealValue().formatPrice(false)
            }
            binding.iTextChartActualValue.text = when{
                it.isEmpty() || it[1].iGetValue() == -1.0 -> "-"
                else -> it[1].iGetRealValue().formatPrice(false)
            }
        }
    }

    /* refresh week id */
    private fun refresh(){
        refreshData(true)
    }

    private fun refreshData(isContinue: Boolean = false){
        val venueID = login.iGetVenueID()
        if(!binding.iTextDaily.isSelected){
            /* set week */
            model.httpGetWeek(venueID,isNeedLoading = !isContinue, finish = {
                AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
                binding.iRefreshRevenue.isRefreshing = false
            }){ week ->
                binding.iTextTargetZValue.text = week.targetPOJO.revenueTarget?.total.formatPrice(false)
                binding.iTextActualValue.text = week.actualPOJO.actualRevenue?.total.formatPrice(false)
                ArrayList<TESTLineEntity>().apply {
                    add(TESTLineEntity(
                        Color.parseColor("#FFFFFF"),
                        "Target",
                        week.targetPOJO.revenueTarget.iGetList()))
                    add(TESTLineEntity(
                        Color.parseColor("#2FCBFF"),
                        "Actual",
                        week.actualPOJO.actualRevenue.iGetList()))
                    refreshChat(true,this)
                }
            }
        }else{
            /* daily */
            model.httpGetTodayAllDataMinutes(venueID,isUseLoading = !isContinue, finish = {
                AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
                binding.iRefreshRevenue.isRefreshing = false
            }){ daily ->
                binding.iTextTargetZValue.text = daily.targetPOJO?.revenueTarget.formatPrice(false)
                binding.iTextActualValue.text = daily.actualPOJO?.actualRevenue.formatPrice(false)
                refreshDailyChat(daily.targetPOJO, daily.actualPOJO)
            }
        }
    }

    private fun refreshDailyChat(targetPOJO: TodayDataEntity.TargetPOJO?, actualPOJO: TodayDataEntity.ActualPOJO?){
        val lines = ArrayList<TESTLineEntity>()
        val default = TESTLineNoteEntity(null,true)
        /* target */
        val size = 97
        val target = Array(size){ default }
        targetPOJO?.revenueTargetHoursCostPOJOList?.let { revenueTarget ->
            var isUseSeq = true
            for (i in revenueTarget){
                if(i.seqNo == null || i.seqNo >= size){
                    isUseSeq = false
                    break
                }
            }
            for(i in revenueTarget.indices){
                val item = revenueTarget[i]
                if(isUseSeq){
                    target[item.seqNo!!] = TESTLineNoteEntity(item.cost, true)
                }else{
                    target[i] = TESTLineNoteEntity(item.cost, true)
                }
            }
        }
        lines.add(TESTLineEntity(
            Color.parseColor("#FFFFFF"),
            "Target",
            target.toList()))
        /* actual */
        val actual = Array(size){ default }
        actualPOJO?.actualRevenueHoursCostPOJOList?.let { revenueActual ->
            var isUseSeq = true
            for (i in revenueActual){
                if(i.seqNo == null || i.seqNo >= size){
                    isUseSeq = false
                    break
                }
            }
            for(i in revenueActual.indices){
                val item = revenueActual[i]
                if(isUseSeq){
                    actual[item.seqNo!!] = TESTLineNoteEntity(item.cost, true)
                }else{
                    actual[i] = TESTLineNoteEntity(item.cost, true)
                }
            }
        }
        lines.add(TESTLineEntity(
            Color.parseColor("#2FCBFF"),
            "Actual",
            actual.toList()))
        refreshChat(false, lines)
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iTextDaily -> {
                binding.iTextDaily.isSelected = true
                binding.iTextWeekly.isSelected = false
                refreshChat(true,null)
                refreshData()
            }
            R.id.iTextWeekly -> {
                binding.iTextWeekly.isSelected = true
                binding.iTextDaily.isSelected = false
                refreshChat(true,null)
                refreshData()
            }
        }
    }

    private fun refreshChat(isWeeks: Boolean,
                            array: ArrayList<TESTLineEntity>?){
        binding.iChartView.apply {
            clear(array == null)
            array?.forEach {
                binding.iChartView.addChart(it.color, it.name, it.list)
            }
            finish(isWeeks, false)
        }
    }

    private fun WeekDetailsEntity.WeekItemValueEntity?.iGetList(isUseHour: Boolean = false):
            ArrayList<TESTLineNoteEntity>{
        val entity = this
        val array = ArrayList<TESTLineNoteEntity>()
        if(entity != null){
            array.add(TESTLineNoteEntity(entity.mon,isUseHour))
            array.add(TESTLineNoteEntity(entity.tue,isUseHour))
            array.add(TESTLineNoteEntity(entity.wed,isUseHour))
            array.add(TESTLineNoteEntity(entity.thu,isUseHour))
            array.add(TESTLineNoteEntity(entity.fri,isUseHour))
            array.add(TESTLineNoteEntity(entity.sat,isUseHour))
            array.add(TESTLineNoteEntity(entity.sun,isUseHour))
        }
        return array
    }
}