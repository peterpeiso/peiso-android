package user.peiso.com.au.ui.home

import i.library.base.base.BaseViewModel
import i.library.base.net.retrofit.APIRequest
import user.peiso.com.au.entity.GuidanceUIEntity
import user.peiso.com.au.entity.InformationEntity
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2022/12/27.
 **/
class UiHomeVModel: BaseViewModel() {

    fun httpAccountList(){
        APIRequest<InformationEntity>(this)
            .loading(APIRequest.REQUEST_LOADING_NOT)
            .request {
                RetrofitService.instant().httpAccountInformation()
            }
            .registerSucceed {
                it?.also { login.iSetUserInformation(it) }
            }
            .start()
    }

}