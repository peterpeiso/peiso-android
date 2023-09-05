package user.peiso.com.au.ui.chart

import android.graphics.Color
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import i.library.base.adapter.IBindAdapter
import i.library.base.base.BaseFragment
import i.library.base.listener.ClickInformBack
import i.library.base.utils.AnimationManager
import user.peiso.com.au.BR
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogWeekBinding
import user.peiso.com.au.databinding.ItemWeekBinding
import user.peiso.com.au.databinding.UiChartBinding
import user.peiso.com.au.entity.TESTLineNoteEntity
import user.peiso.com.au.entity.WeekDetailsEntity
import user.peiso.com.au.entity.WeekItemEntity
import user.peiso.com.au.extend.iShowWindows
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.httpGetWeek
import user.peiso.com.au.net.httpGetWeekList

/**
 * Created by forever_zero on 2023/1/6.
 **/
class UiChartFragment: BaseFragment<UiChartBinding,UiChartVModel>(), ClickInformBack {

    private val isRevenue by lazy{
        arguments?.getBoolean("isRevenue")?:false
    }

    private val venueID = login.iGetVenueID()

    override val layoutID: Int
        get() = R.layout.ui_chart

    override val mViewModel: UiChartVModel
        get() = ViewModelProvider(this)[UiChartVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
        binding.iTextName.text = if(isRevenue) "Weekly Revenue" else "Weekly Labour"
        if(isRevenue) binding.iTextName.setCompoundDrawables(null,null,null,null)
        binding.iRefreshChart.setOnRefreshListener {
            refresh()
        }
        refresh()
    }

    private fun refresh(){
        model.httpGetWeekList(venueID,false){ list ->
            model.iWeekList = list
            refreshData(true)
        }
    }

    private fun refreshData(isContinue: Boolean = false){
//        model.iGetWeek()?.dashBoardId?.also { id ->
//            /* set week */
//            model.httpGetWeek(venueID,id,!isContinue,{
//                AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
//                binding.iRefreshChart.isRefreshing = false
//            }){ week ->
//                binding.iViewChart.clear()
//                if(isRevenue){
//                    binding.iViewChart.addChart(Color.parseColor("#FFFFFF"), "Target",
//                        iGetList(week.targetPOJO.revenueTarget))
//                    binding.iViewChart.addChart(Color.parseColor("#2FCBFF"), "Actual",
//                        iGetList(week.actualPOJO.actualRevenue))
//                }else if(!binding.iTextName.isSelected){
//                    binding.iViewChart.addChart(Color.parseColor("#FFFFFF"), "Target",
//                        iGetList(week.targetPOJO.labourPOJO?.cost))
//                    binding.iViewChart.addChart(Color.parseColor("#2FCBFF"), "Actual",
//                        iGetList(week.actualPOJO.labourPOJO?.cost))
//                }else{
//                    binding.iViewChart.addChart(Color.parseColor("#FFFFFF"), "Target",
//                        iGetList(week.targetPOJO.labourPOJO?.hours,true))
//                    binding.iViewChart.addChart(Color.parseColor("#2FCBFF"), "Actual",
//                        iGetList(week.actualPOJO.labourPOJO?.hours,true))
//                }
//            }
//        }
    }

    private fun iGetList(entity: WeekDetailsEntity.WeekItemValueEntity?,isUseHour: Boolean = false):
            ArrayList<TESTLineNoteEntity>{
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

    override fun onValidClick(v: View) {
        when (v.id) {
            R.id.iTextName -> {
                binding.iTextName.isSelected = !binding.iTextName.isSelected
                refreshData()
            }
            R.id.iImageBack -> {
                nav().navigateUp()
            }
            R.id.iTextScope -> {
                val list = model.iWeekList ?: return
                binding.iTextScope.isSelected = true
                binding.iTextScope.iShowWindows<DialogWeekBinding>(R.layout.dialog_week, false) {
                        binding, window ->
                    window.setOnDismissListener {
                        this.binding.iTextScope.isSelected = false
                    }
                    val adapter by lazy {
                        IBindAdapter<WeekItemEntity, ItemWeekBinding>(BR.dataWeek, R.layout.item_week)
                    }
                    binding.iRecyclerWeek.layoutManager = LinearLayoutManager(requireContext())
                    binding.iRecyclerWeek.adapter = adapter
                    adapter.refreshAdapterData(list)
                    adapter.bindingHolder = {
                            itemBinding, _, entity ->
                        val color = if(entity.weekFlag != 2) "#80FFFFFF" else "#EA3796"
                        itemBinding.iTextVenue.setTextColor(Color.parseColor(color))
                    }
                    adapter.bindingClick = {
                        model.iWeek.set(it.value)
                        model.iWeekType = it.weekFlag
                        window.dismiss()
                        refreshData()
                    }
                }
            }
        }
    }
}