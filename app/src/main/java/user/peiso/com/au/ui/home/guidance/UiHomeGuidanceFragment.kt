package user.peiso.com.au.ui.home.guidance

import android.graphics.Color
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import i.library.base.base.BaseFragment
import i.library.base.listener.ClickInformBack
import i.library.base.utils.AnimationManager
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiHomeGuidanceBinding
import user.peiso.com.au.enums.GuidanceTypeEnum
import user.peiso.com.au.extend.login

/**
 * Created by forever_zero on 2022/12/28.
 **/
class UiHomeGuidanceFragment: BaseFragment<UiHomeGuidanceBinding,UiHomeGuidanceVModel>(),
    ClickInformBack {

    private val iColorPositive = Color.parseColor("#34C759")
    private val iColorExpected = Color.parseColor("#FF9500")

    override val layoutID: Int
        get() = R.layout.ui_home_guidance

    override val mViewModel: UiHomeGuidanceVModel
        get() = ViewModelProvider(this)[UiHomeGuidanceVModel::class.java]

    override fun initViews() {
        binding.click = this
        binding.model = model
        model.type.observe(this){
            refreshUI(it)
        }
        binding.iRefreshGuidance.setOnRefreshListener {
            refresh()
        }
        login.venue.observe(this){
            it?:return@observe
            if(!binding.iLayoutLoading.isVisible){
                binding.iRefreshGuidance.isRefreshing = true
            }
            refresh()
        }
    }

    private fun refresh(){
        model.httpGetGuidance(
            {
                AnimationManager.alphaAnimation(binding.iLayoutLoading,false)
                binding.iRefreshGuidance.isRefreshing = false
            }){
            refreshUI(model.type.value!!)
        }
    }

    private fun refreshUI(type: GuidanceTypeEnum){
        binding.apply {
            this@UiHomeGuidanceFragment.model.details.get()?.also { details ->
                if(!binding.iLayoutLoading.isVisible){
                    TransitionManager.beginDelayedTransition(binding.iLayoutContent)
                }
                /* guidance */
                val iGetGuidance = details
                    .iGetGuidance(type.id == GuidanceTypeEnum.Labour.id)
                /* message */
                val isCompleteA = details.isComplete(type.id == GuidanceTypeEnum.Labour.id)
                val messageColor = if(isCompleteA) iColorPositive else iColorExpected
                iTextDailyDetails.setTextColor(messageColor)
                iTextDailyDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,if(isCompleteA) 16f else 24f)
                iTextDailyDetails.text = iGetGuidance
                /* title */
                iTextDaily.text = type.title
                iTextDaily.isVisible = !isCompleteA
                /* value */
                iTextRevenueValue.text = details.iGetRevenue()
                iTextLabourValue.text = details.iGetLabour()
            }
        }
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iImageChange -> {
                model.type.value = if(model.type.value?.id == GuidanceTypeEnum.Revenue.id){
                    GuidanceTypeEnum.Labour
                }else{
                    GuidanceTypeEnum.Revenue
                }
            }
        }
    }
}