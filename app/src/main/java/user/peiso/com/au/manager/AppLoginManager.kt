package user.peiso.com.au.manager

import android.text.TextUtils
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import i.library.base.constant.APPDataConstant.DATA_USER_INFO
import i.library.base.constant.APPDataConstant.DATA_USER_TOKEN
import i.library.base.constant.APPDataConstant.iGetData
import i.library.base.constant.APPDataConstant.iSetData
import i.library.base.listener.LoginManagerListener
import i.library.base.manager.ControlActivityManger
import user.peiso.com.au.entity.InformationEntity
import user.peiso.com.au.ui.MainActivity

/**
 * Created by forever_zero on 2022/9/21.
 **/
class AppLoginManager: LoginManagerListener {

    private var token: String? = null
    var information: InformationEntity? = null

    val venue by lazy{
        MutableLiveData<InformationEntity.VenueUserInfo?>()
    }

    fun iGetVenueID(): String{
        val iGetSelectVenue = iGetSelectVenue()
        return iGetSelectVenue?.iGetVenueID()?:""
    }

    fun iGetSelectVenue(): InformationEntity.VenueUserInfo?{
        return venue.value?:information?.iGetVenue()
    }

    fun iSetSelectVenue(info: InformationEntity.VenueUserInfo?){
        venue.value = info
        information?.venue = info
        iSetUserInformation(information)
    }

    fun initThis(callback: (AppLoginManager) -> Unit){
        val run = {
            callback.invoke(this)
        }
        var count = 0
        DATA_USER_INFO.iGetData {
            this.information = Gson().fromJson(it, InformationEntity::class.java)
            if(this.information != null){
                this.venue.value = when{
                    this.information?.venue != null -> {
                        this.information?.venue!!
                    }
                    this.information?.venueUserInfoList?.isNotEmpty() == true -> {
                        this.information?.venueUserInfoList?.get(0)
                    }
                    else -> null
                }
            }
            count ++
            if(count == 2) run.invoke()
        }
        DATA_USER_TOKEN.iGetData {
            token = it
            count ++
            if(count == 2) run.invoke()
        }
    }

    fun isLogin(): Boolean{
        return !TextUtils.isEmpty(token)
    }

    override fun iGetToken(): String {
        return token?:""
    }

    override fun iOutLogin(code: Int, isToLogin: Boolean) {
        iSetToken(null)
        iSetUserInformation(null)
        if(isToLogin){
            ControlActivityManger.getCurrentActivity()?.let {
                if(it is MainActivity){
                    it.startLogin(bundle = bundleOf("code" to code))
                }
            }
        }
    }

    fun iSetToken(token: String?){
        this.token = token
        DATA_USER_TOKEN.iSetData(token)
    }

    fun iSetUserInformation(information: InformationEntity?){
        this.information = information
        /* venue */
        var isHas = false
        information?.venueUserInfoList?.apply {
            for(i in information.venueUserInfoList){
                if(venue.value?.venueId == i.venueId){
                    isHas = true
                    break
                }
            }
        }
        when{
            information == null -> venue.value = null
            !isHas && !information.venueUserInfoList.isNullOrEmpty() -> {
                venue.value = information.venueUserInfoList[0]
            }
        }
        information?.venue = venue.value
        val toJson = Gson().toJson(information)
        DATA_USER_INFO.iSetData(toJson)
    }
}