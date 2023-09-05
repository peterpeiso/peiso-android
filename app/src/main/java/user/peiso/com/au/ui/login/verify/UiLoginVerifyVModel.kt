package user.peiso.com.au.ui.login.verify

import androidx.databinding.ObservableField
import i.library.base.base.BaseViewModel
import i.library.base.net.retrofit.APIRequest
import user.peiso.com.au.R
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2022/12/26.
 **/
class UiLoginVerifyVModel : BaseViewModel(){

    var code = ""

    var recode = ObservableField(getString(R.string.resend))

    fun httpCheckCode(mobile: String,succeed: (code: String) -> Unit){
        APIRequest<Any>(this)
            .loading("check code")
            .request {
                RetrofitService.instant().httpCheckCode(mobile, code)
            }
            .registerSucceed {
                succeed.invoke(code)
            }
            .start()
    }


}