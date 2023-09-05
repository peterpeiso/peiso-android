package user.peiso.com.au.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.core.os.bundleOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import i.library.base.base.BaseActivity
import i.library.base.constant.APPDataConstant.iGetData
import i.library.base.constant.APPDataConstant.iSetData
import i.library.base.expands.logo
import i.library.base.expands.navigationTo
import i.library.base.utils.AMathUtils
import i.library.base.utils.AMathUtils.formatHours
import i.library.base.utils.AMathUtils.formatPrice
import user.peiso.com.au.R
import user.peiso.com.au.databinding.ActivityMainBinding
import user.peiso.com.au.entity.TodayDataEntity
import user.peiso.com.au.extend.iGetAlphaNavOptions
import user.peiso.com.au.extend.login
import user.peiso.com.au.extend.toCheckUpdate
import user.peiso.com.au.net.httpCheckHasPassword
import user.peiso.com.au.net.httpTodaySummary
import user.peiso.com.au.ui.login.login.UiLoginFragment

/**
 * Created by forever_zero on 2022/12/22.
 **/
class MainActivity : BaseActivity<ActivityMainBinding>(){

    companion object{

        const val delay = 1000L

        var isNeedDisposePage = false

        val DATA_LAST_SUMMARY = stringPreferencesKey("data_last_summary")

        var iRefreshTodayDataTime = 15

    }

    lateinit var controller: NavController

    override val layoutID: Int
        get() = R.layout.activity_main

    override fun initViews() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.iContainerMain) as NavHostFragment
        controller = fragment.navController
        onNewIntent(intent)
        /* go check update */
        binding.root.postDelayed({
            this.toCheckUpdate()
        },delay)
    }

    private fun toggleGraph(id: Int, oldStart: Int, bundle: Bundle?= null, iNavOptions: NavOptions? = null){
        "------------------ toggleGraph: $id --------------------".logo()
        controller.popBackStack(oldStart,true)
        controller.navigationTo(id,bundle,iNavOptions)
    }

    fun startApp(isInWelcome: Boolean = false,
                 bundle: Bundle ?= null,
                 iNavOptions: NavOptions ?= iGetAlphaNavOptions()
    ){
        model.httpTodaySummary(login.iGetVenueID(),!isInWelcome){
            iRefreshTodayDataTime = if(it.appRefreshTimeMinutes == 0) 15  else it.appRefreshTimeMinutes
            if(it.appTodaySummaryShowFlag == true){
                val b = bundle ?: Bundle()
                b.putString("revenue", it.actualPOJO.actualRevenue.formatPrice(false))
                b.putString("labour", it.actualPOJO.labourPOJO.cost.formatPrice(false))
                b.putString("labour_hours",it.actualPOJO.labourPOJO.hours.formatHours(false))
                val id = if(isInWelcome) R.id.fragment_welcome else R.id.fragment_login
                toggleGraph(R.id.fragment_today, id, b, iNavOptions)
                DATA_LAST_SUMMARY.iSetData("${System.currentTimeMillis()}")
            }else{
                val id = if(isInWelcome) R.id.fragment_welcome else R.id.fragment_login
                toggleGraph(R.id.fragment_home, id, bundle, iNavOptions)
            }
        }
    }

    fun startLogin(isInWelcome: Boolean = false,
                   bundle: Bundle ?= null,
                   iNavOptions: NavOptions ?= null){
        if(controller.currentDestination?.id == R.id.fragment_login){
            val fragment =
                supportFragmentManager.findFragmentById(R.id.iContainerMain) as NavHostFragment
            val fragments = fragment.childFragmentManager.fragments
            for(i in fragments){
                if(i is UiLoginFragment){
                    i.reCode(bundle)
                }
            }
        }else{
            val id = if(isInWelcome || controller.currentDestination?.id == R.id.fragment_welcome) {
                R.id.fragment_welcome
            } else R.id.fragment_home
            toggleGraph(R.id.fragment_login, id,bundle,iNavOptions)
        }
    }

    override fun getAppController(): NavController {
        return controller
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?:return
        val mobile = intent.data?.getQueryParameter("phonenumber")
        if(!TextUtils.isEmpty(mobile)){
            isNeedDisposePage = true
            model.httpCheckHasPassword(mobile!!){ isHasPassword ->
                val bundle = bundleOf("mobile" to mobile)
                val id = when{
                    controller.currentDestination?.id == R.id.fragment_welcome -> {
                        R.id.fragment_welcome
                    }
                    login.isLogin() -> {
                        R.id.fragment_home
                    }
                    else -> {
                        R.id.fragment_login
                    }
                }
                login.iOutLogin(0,false)
                if(isHasPassword){
                    toggleGraph(R.id.fragment_login, id, bundle)
                }else{
                    toggleGraph(R.id.fragment_login_create, id, bundle)
                }
            }
        }
    }
}