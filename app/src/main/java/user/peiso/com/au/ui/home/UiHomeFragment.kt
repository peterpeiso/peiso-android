package user.peiso.com.au.ui.home

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import i.library.base.adapter.IBindAdapter
import i.library.base.adapter.PagerAdapter
import i.library.base.base.BaseApplication
import i.library.base.base.BaseFragment
import i.library.base.listener.ClickInformBack
import i.library.base.utils.TimedTaskUtils
import user.peiso.com.au.BR
import user.peiso.com.au.R
import user.peiso.com.au.constants.INFORM_REFRESH_TODAY
import user.peiso.com.au.databinding.DialogAccountBinding
import user.peiso.com.au.databinding.ItemVenueBinding
import user.peiso.com.au.databinding.UiHomeBinding
import user.peiso.com.au.entity.InformationEntity
import user.peiso.com.au.extend.iShowWindows
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.httpTodaySummary
import user.peiso.com.au.ui.MainActivity
import user.peiso.com.au.ui.dialog.AccountVModel
import user.peiso.com.au.ui.dialog.DeleteAccountDialog
import user.peiso.com.au.ui.home.guidance.UiHomeGuidanceFragment
import user.peiso.com.au.ui.home.labour.UiHomeLabourFragment
import user.peiso.com.au.ui.home.revenue.UiHomeRevenueFragment
import user.peiso.com.au.ui.today.UiTodaySummaryFragment

/**
 * Created by forever_zero on 2022/12/27.
 **/
class UiHomeFragment: BaseFragment<UiHomeBinding,UiHomeVModel>(), ClickInformBack {

    private val list by lazy {
        ArrayList<Fragment>().apply {
            add(UiHomeGuidanceFragment())
            add(UiHomeRevenueFragment())
            add(UiHomeLabourFragment())
        }
    }

    private val timeTaskUtils by lazy {
        TimedTaskUtils()
    }

    override val layoutID: Int
        get() = R.layout.ui_home

    override val mViewModel: UiHomeVModel
        get() = ViewModelProvider(this)[UiHomeVModel::class.java]

    override fun initViews() {
        binding.click = this
        /* venue */
        login.venue.observe(this){ venue ->
            binding.iTextName.text = venue?.venueName
            binding.iTextNameFirst.text = venue?.venueName?.substring(0,1)?.uppercase()
        }
        binding.iViewIndicator.iSetSelect(0)
        /* pager */
        binding.iPagerHome.isSaveEnabled = false
        binding.iPagerHome.isUserInputEnabled = true
        binding.iPagerHome.adapter = PagerAdapter(requireActivity(),list)
        binding.iViewIndicator.iSetLen(list.size)
        binding.iPagerHome.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.iViewIndicator.iSetSelect(position)
            }
        })
        model.httpAccountList()
        /* Refresh every 15 minutes */
        val interval = MainActivity.iRefreshTodayDataTime * 60 * 1000L
        timeTaskUtils.timeCountDownRun(interval, -1) {
            /* get information */
//            if (nav().currentDestination?.id == R.id.fragment_today) {
//                BaseApplication.toSendInform(INFORM_REFRESH_TODAY)
//            }else{
//                model.httpTodaySummary(login.iGetVenueID(), false) {
//                    UiTodaySummaryFragment.launch(nav(), it)
//                }
//            }
            /* refresh */
            val venue = Gson().toJson(login.iGetSelectVenue())
            val info = Gson().fromJson(venue, InformationEntity.VenueUserInfo::class.java)
            login.iSetSelectVenue(info)
        }
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iTextName -> {
                val model = ViewModelProvider(this)[AccountVModel::class.java]
                val adapter by lazy {
                    IBindAdapter<InformationEntity.VenueUserInfo, ItemVenueBinding>(BR.dataVenue,R.layout.item_venue)
                }
                binding.iTextName.isSelected = true
                binding.iTextName.iShowWindows<DialogAccountBinding>(R.layout.dialog_account){ binding,window ->
                    window.setOnDismissListener {
                        this.binding.iTextName.isSelected = false
                    }
                    binding.click = object: ClickInformBack{
                        override fun onValidClick(v: View) {
                            when(v.id){
                                R.id.iBtnLogOut -> {
                                    model.httpUserLogOut {
                                        window.dismiss()
                                        login.iOutLogin(0,true)
                                    }
                                }
                                R.id.iTextDeleteAccount -> {
                                    window.dismiss()
                                    DeleteAccountDialog{
                                        model.httpAccountDelete {
                                            login.iOutLogin(-1,true)
                                        }
                                    }.show(childFragmentManager,"Delete")
                                }
                            }
                        }
                    }
                    val list = login.information?.venueUserInfoList?:ArrayList()
                    binding.iTextVenue.isVisible = list.size > 1
                    binding.iScrollVenues.isVisible = list.size > 1
                    binding.iViewLine.isVisible = list.size > 1
                    if(list.isEmpty()){
                        binding.iTextVenue.visibility = View.GONE
                        binding.iScrollVenues.visibility = View.GONE
                    }else{
                        binding.iRecycleVenues.layoutManager = LinearLayoutManager(requireContext())
                        binding.iRecycleVenues.adapter = adapter
                        adapter.refreshAdapterData(list)
                        adapter.bindingClick = {
                            login.iSetSelectVenue(it)
                            window.dismiss()
                        }
                    }
                }
            }
        }
    }

}