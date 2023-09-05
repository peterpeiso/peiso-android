package user.peiso.com.au.ui.login.login

import i.library.base.base.BaseViewModel
import i.library.base.constant.TOKEN_NAME
import i.library.base.expands.toast
import i.library.base.net.retrofit.APIRequest
import i.library.base.utils.CheckUtils
import i.library.base.utils.MD5Utils
import user.peiso.com.au.entity.InformationEntity
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2022/12/23.
 **/
class UiLoginVModel: BaseViewModel() {

    var mobile = ""

    var password = ""

    fun httpLogin(callback: (InformationEntity?) -> Unit){
        var isContinue = false
        val realMobile: String = mobile.replace(" ","")
        val realPassword: String = password//"123456"
        when{
            realMobile == "" -> {
                "Please enter mobile number".toast()
            }
            !CheckUtils.checkPhoneNumber(realMobile) -> {
                "Please enter the correct mobile number".toast()
            }
            realPassword.length !in 4..6 -> {
                "Please enter a 4-6 digit passcode".toast()
            }
            else -> isContinue = true
        }
        if(!isContinue) return
        APIRequest<InformationEntity>(this)
            .loading("login")
            .request {
                val map = hashMapOf("mobileNumber" to realMobile,"password" to MD5Utils.md5(realPassword))
                RetrofitService.instant().httpLogin(map)
            }
            .registerSucceedApi {
                if(it?.isSucceed() == true){
                    if(it.data?.venueUserInfoList?.isEmpty() == false){
                        val token = it.headers[TOKEN_NAME]?:""
                        login.apply {
                            iSetToken(token)
                            iSetUserInformation(it.data)
                        }
                        callback.invoke(it.data)
                    }else{
                        "There are no venues associated with this account.".toast()
                    }
                }else{
                    it?.message?.toast()
                }
            }
            .start()
    }
}