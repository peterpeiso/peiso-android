package user.peiso.com.au.ui.home.guidance

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import i.library.base.base.BaseViewModel
import i.library.base.net.retrofit.APIRequest
import user.peiso.com.au.entity.GuidanceUIEntity
import user.peiso.com.au.enums.GuidanceTypeEnum
import user.peiso.com.au.extend.login
import user.peiso.com.au.net.RetrofitService

/**
 * Created by forever_zero on 2022/12/28.
 **/
class UiHomeGuidanceVModel: BaseViewModel(){

    val type by lazy {
        MutableLiveData(GuidanceTypeEnum.Revenue)
    }

    val details = ObservableField<GuidanceUIEntity>()

    fun httpGetGuidance(finish: () -> Unit,callback: () -> Unit){
        APIRequest<GuidanceUIEntity>(this)
            .loading(APIRequest.REQUEST_LOADING_NOT)
            .request {
                RetrofitService.instant().httpGetGuidance(login.iGetVenueID())
            }
            .registerSucceed {
                details.set(it)
                callback.invoke()
            }
            .registerFinish(finish)
            .start()
    }
}