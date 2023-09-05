package user.peiso.com.au.ui.home.labour

import android.graphics.Color
import android.view.View
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.listener.ClickInformBack
import i.library.base.utils.AMathUtils.formatHours
import i.library.base.utils.AMathUtils.formatPrice
import i.library.base.utils.AnimationManager
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiHomeLabourBinding
import user.peiso.com.au.entity.TESTLineEntity
import user.peiso.com.au.entity.TESTLineNoteEntity
import user.peiso.com.au.entity.TodayDataEntity
import user.peiso.com.au.entity.WeekDetailsEntity
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.httpGetDaily
import user.peiso.com.au.net.httpGetTodayAllData
import user.peiso.com.au.net.httpGetTodayAllDataMinutes
import user.peiso.com.au.net.httpGetWeek
import user.peiso.com.au.ui.MainActivity

/**
 * Created by forever_zero on 2022/12/28.
 **/
class UiHomeLabourFragment: BaseFragment<UiHomeLabourBinding,UiHomeLabourVModel>(),
    ClickInformBack {

    override val layoutID: Int
        get() = R.layout.ui_home_labour

    override val mViewModel: UiHomeLabourVModel
        get() = ViewModelProvider(this)[UiHomeLabourVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
        binding.iTextDaily.isSelected = true
        binding.iTextWeekly.isSelected = false
        binding.iRefreshGuidance.setOnRefreshListener {
            refresh()
        }
        login.venue.observe(this){
            it?:return@observe
            binding.iRefreshGuidance.isRefreshing = true
            refresh()
        }
        binding.iChartView.chooses = {
            val isNeedHour = binding.iTextTitle.isSelected
            binding.iTextChartTargetValue.text = when{
                it.isEmpty() || it[0].iGetValue() == -1.0 -> "-"
                !isNeedHour -> it[0].iGetRealValue().formatPrice(false)
                else -> it[0].iGetRealValue().formatHours(false)
            }
            binding.iTextChartActualValue.text = when{
                it.size < 2 || it[1].iGetValue() == -1.0 -> "-"
                !isNeedHour -> it[1].iGetRealValue().formatPrice(false)
                else -> it[1].iGetRealValue().formatHours(false)
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
            model.httpGetWeek(venueID, isNeedLoading = !isContinue, finish = {
                AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
                binding.iRefreshGuidance.isRefreshing = false
            }){ week ->
                val isNeedHour = binding.iTextTitle.isSelected
                binding.iTextTargetValue.text = week.targetPOJO.rosterTargetPOJO
                    .let { if(isNeedHour) it?.hours?.total.formatHours(false)
                    else it?.cost?.total.formatPrice(false) }
                binding.iTextTargetZValue.text = week.targetPOJO.labourPOJO
                    .let { if(isNeedHour) it?.hours?.total.formatHours(false)
                    else it?.cost?.total.formatPrice(false) }
                binding.iTextActualValue.text = week.actualPOJO.labourPOJO
                    .let { if(isNeedHour) it?.hours?.total.formatHours(false)
                    else it?.cost?.total.formatPrice(false) }
                ArrayList<TESTLineEntity>().apply {
                    val target = if(!isNeedHour)
                        week.targetPOJO.labourPOJO?.cost
                    else week.targetPOJO.labourPOJO?.hours
                    add(TESTLineEntity(
                            Color.parseColor("#FFFFFF"), "Target",
                            iGetList(target, isNeedHour))
                    )
                    val actual = if(!isNeedHour)
                        week.actualPOJO.labourPOJO?.cost
                    else week.actualPOJO.labourPOJO?.hours
                    add(TESTLineEntity(
                        Color.parseColor("#2FCBFF"), "Actual",
                        iGetList(actual, isNeedHour))
                    )
                    refreshChat(true,isNeedHour,this)
                }
            }
        }else{
            /* daily */
            model.httpGetTodayAllDataMinutes(venueID,isUseLoading = !isContinue, finish = {
                AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
                binding.iRefreshGuidance.isRefreshing = false
            }){ daily ->
                val isNeedHour = binding.iTextTitle.isSelected
                binding.iTextTargetValue.text = daily.targetPOJO?.rosterTargetPOJO
                    .let { if(isNeedHour) it?.hours.formatHours(false) else it?.cost.formatPrice(false) }
                binding.iTextTargetZValue.text = daily.targetPOJO?.labourPOJO
                    .let { if(isNeedHour) it?.hours.formatHours(false) else it?.cost.formatPrice(false) }
                binding.iTextActualValue.text = daily.actualPOJO?.labourPOJO
                    .let { if(isNeedHour) it?.hours.formatHours(false) else it?.cost.formatPrice(false) }
                refreshDailyChat(daily.targetPOJO,daily.actualPOJO)
//                model.httpGetTodayAllDataMinutes(venueID, isUseLoading = !isContinue,  finish = {
//                    AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
//                    binding.iRefreshGuidance.isRefreshing = false
//                }){
//                }
            }
        }
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iImageChange -> {
                val isNeedHour = binding.iTextTitle.isSelected
                binding.iTextTitle.isSelected = !binding.iTextTitle.isSelected
                binding.iChartView.refreshChoose()
                refreshChat(true, isNeedHour,null)
                refreshData()
            }
            R.id.iTextDaily -> {
                binding.iTextDaily.isSelected = true
                binding.iTextWeekly.isSelected = false
                refreshData()
            }
            R.id.iTextWeekly -> {
                val isNeedHour = binding.iTextTitle.isSelected
                binding.iTextWeekly.isSelected = true
                binding.iTextDaily.isSelected = false
                refreshChat(true, isNeedHour,null)
                refreshData()
            }
        }
    }


    private fun refreshDailyChat(targetPOJO: TodayDataEntity.TargetPOJO?, actualPOJO: TodayDataEntity.ActualPOJO?){
        val lines = ArrayList<TESTLineEntity>()
        val default = TESTLineNoteEntity(null,true)
        val isNeedHour = binding.iTextTitle.isSelected
        /* target */
        val size = 97
        val target = Array(size){ default }
        targetPOJO?.labourTargetHoursCostPOJOList?.let { revenueTarget ->
            var isUseSeq = true
            for (i in revenueTarget){
                if(i.seqNo == null || i.seqNo >= size){
                    isUseSeq = false
                    break
                }
            }
            for(i in revenueTarget.indices){
                val item = revenueTarget[i]
                val hours = if(item.hours == null) null else (item.hours / 60)
                if(isUseSeq){
                    target[item.seqNo!!] = TESTLineNoteEntity(
                        if(isNeedHour) hours else item.cost, isNeedHour)
                }else{
                    target[i] = TESTLineNoteEntity(
                        if(isNeedHour) hours else item.cost, isNeedHour)
                }
            }
        }
        lines.add(
            TESTLineEntity(
            Color.parseColor("#FFFFFF"),
            "Target",
            target.toList())
        )
        /* actual */
        val actual = Array(size){ default }
        actualPOJO?.actualLabourHoursCostPOJOList?.let { revenueActual ->
            var isUseSeq = true
            for (i in revenueActual){
                if(i.seqNo == null || i.seqNo >= size){
                    isUseSeq = false
                    break
                }
            }
            for(i in revenueActual.indices){
                val item = revenueActual[i]
                val hours = if(item.hours == null) null else (item.hours / 60)
                if(isUseSeq){
                    actual[item.seqNo!!] = TESTLineNoteEntity(
                        if(isNeedHour) hours else item.cost, isNeedHour)
                }else{
                    actual[i] = TESTLineNoteEntity(
                        if(isNeedHour) hours else item.cost, isNeedHour)
                }
            }
        }
        lines.add(
            TESTLineEntity(
            Color.parseColor("#2FCBFF"),
            "Actual",
            actual.toList())
        )
        refreshChat(false, isNeedHour, lines)
    }

    private fun refreshChat(isWeeks: Boolean,
                            isHours: Boolean,
                            array: ArrayList<TESTLineEntity>?){
        binding.iChartView.apply {
            clear(array == null)
            array?.forEach {
                binding.iChartView.addChart(it.color, it.name, it.list)
            }
            finish(isWeeks,isHours)
        }
    }

    private fun iGetList(entity: WeekDetailsEntity.WeekItemValueEntity?, isUseHour: Boolean = false):
            ArrayList<TESTLineNoteEntity>{
        val array = ArrayList<TESTLineNoteEntity>()
        if(entity != null){
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.mon), isUseHour))
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.tue), isUseHour))
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.wed), isUseHour))
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.thu), isUseHour))
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.fri), isUseHour))
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.sat), isUseHour))
            array.add(TESTLineNoteEntity(isUseHour.iGetValue(entity.sun), isUseHour))
        }
        return array
    }

    private fun Boolean.iGetValue(value: Double?): Double?{
        return when{
            value == null -> null
            this -> value /// 60
            else -> value
        }
    }
}