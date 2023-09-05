package user.peiso.com.au.ui.login.forgot

import i.library.base.base.BaseViewModel
import i.library.base.expands.toast
import i.library.base.net.retrofit.APIRequest
import i.library.base.utils.MD5Utils
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2022/12/23.
 **/
class UiLoginForgotVModel: BaseViewModel() {

    var passcode: String = ""

    var passcodeNew: String = ""

    fun httpResetPassword(code: String, mobile: String,succeed: () -> Unit){
        when{
            passcode.length !in 4..6 -> {
                "Please enter a 4-6 digit passcode".toast()
            }
            passcode != passcodeNew -> {
                "2 passcodes entered are different".toast()
            }
        }
        APIRequest<Any>(this)
            .loading("reset")
            .request {
                val map = hashMapOf("code" to code, "mobileNumber" to mobile, "password" to MD5Utils.md5(passcode))
                RetrofitService.instant().httpResetPassword(map)
            }
            .registerSucceed {
                succeed.invoke()
            }
            .start()
    }
}