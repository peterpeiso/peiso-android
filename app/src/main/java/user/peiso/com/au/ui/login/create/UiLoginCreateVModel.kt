package user.peiso.com.au.ui.login.create

import i.library.base.base.BaseViewModel
import i.library.base.expands.logo
import i.library.base.expands.toast
import i.library.base.net.retrofit.APIRequest
import i.library.base.utils.MD5Utils
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2023/1/6.
 **/
class UiLoginCreateVModel: BaseViewModel() {

    var passcode = ""

    fun httpUserPassword(mobile: String, callback: () -> Unit){
        val code = passcode
        if(code.length !in 4..6){
            "Please enter a 4-6 digit passcode".toast()
            return
        }
        val realPasscode = MD5Utils.md5(code)
        APIRequest<Any>(this)
            .loading("set password")
            .request {
                val map = hashMapOf("mobileNumber" to mobile, "password" to realPasscode)
                RetrofitService.instant().httpUserPassword(map)
            }
            .registerSucceedApi {
                "=========".logo()
                callback.invoke()
            }
            .start()
    }
}