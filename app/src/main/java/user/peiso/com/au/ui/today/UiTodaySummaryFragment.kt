package user.peiso.com.au.ui.today

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import i.library.base.base.BaseFragment
import i.library.base.base.NullVModel
import i.library.base.expands.navigationTo
import i.library.base.listener.ClickInformBack
import i.library.base.utils.AMathUtils.formatHours
import i.library.base.utils.AMathUtils.formatPrice
import user.peiso.com.au.R
import user.peiso.com.au.constants.INFORM_REFRESH_TODAY
import user.peiso.com.au.databinding.UiTodayInformtionBinding
import user.peiso.com.au.entity.TodaySummaryEntity
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.httpTodaySummary

/**
 * Created by forever_zero on 2023/2/1.
 **/
class UiTodaySummaryFragment: BaseFragment<UiTodayInformtionBinding,NullVModel>(), ClickInformBack {

    companion object{
        fun launch(nav: NavController,entity: TodaySummaryEntity){
            val b = Bundle()
            b.putString("revenue", entity.actualPOJO.actualRevenue.formatPrice(false))
            b.putString("labour", entity.actualPOJO.labourPOJO.cost.formatPrice(false))
            b.putString("labour_hours", entity.actualPOJO.labourPOJO.hours.formatHours(false))
            nav.navigationTo(R.id.fragment_today, b)
        }
    }

    private lateinit var revenue: String

    private lateinit var labour :String

    private lateinit var labourHours: String

    override val layoutID: Int
        get() = R.layout.ui_today_informtion

    override val mViewModel: NullVModel
        get() = ViewModelProvider(this)[NullVModel::class.java]

    override fun initViews() {
        revenue = arguments?.getString("revenue")?:""
        labour = arguments?.getString("labour")?:""
        labourHours = arguments?.getString("labour_hours")?:""
        binding.click = this
        refreshThis()
    }

    /* data */
    private fun refresh(){
        model.httpTodaySummary(login.iGetVenueID(), true){
            revenue = it.actualPOJO.actualRevenue.formatPrice(false)
            labour = it.actualPOJO.labourPOJO.cost.formatPrice(false)
            labourHours = it.actualPOJO.labourPOJO.hours.formatHours(false)
        }
    }

    private fun refreshThis(){
        binding.iTextRevenueValue.text = revenue
        binding.iTextLabourValue.text =
            if(!binding.iTextLabourValue.isSelected) labour else labourHours
    }

    override fun onAPPVModelInform(key: String, value: Any?) {
        super.onAPPVModelInform(key, value)
        when(key){
            INFORM_REFRESH_TODAY -> {
                refresh()
            }
        }
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iImageLabourValue -> {
                binding.iTextLabourValue.isSelected = !binding.iTextLabourValue.isSelected
                binding.iTextLabourValue.text = if(!binding.iTextLabourValue.isSelected) labour else labourHours
            }
            R.id.iBtnConfirm -> {
                if(!nav().popBackStack(R.id.fragment_home,false)){
                    nav().popBackStack(R.id.fragment_today, true)
                    nav().navigationTo(R.id.fragment_home, arguments)
                }
            }
        }
    }

}