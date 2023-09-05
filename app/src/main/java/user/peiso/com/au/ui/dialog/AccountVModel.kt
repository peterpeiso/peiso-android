package user.peiso.com.au.ui.dialog

import i.library.base.base.BaseViewModel
import i.library.base.net.retrofit.APIRequest
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2022/12/29.
 **/
class AccountVModel : BaseViewModel() {

    fun httpUserLogOut(callback: () -> Unit) {
        APIRequest<Any>(this)
            .loading("log out")
            .request {
                RetrofitService.instant().httpUserLogOut()
            }
            .registerSucceed {
                callback.invoke()
            }
            .start()
    }

    fun httpAccountDelete(callback: () -> Unit) {
        APIRequest<Any>(this)
            .loading("account delete")
            .request {
                RetrofitService.instant().httpAccountDelete()
            }
            .registerSucceed {
                callback.invoke()
            }
            .start()
    }
}