package user.peiso.com.au.net

import i.library.base.base.BaseViewModel
import i.library.base.net.retrofit.APIRequest
import user.peiso.com.au.BuildConfig
import user.peiso.com.au.entity.*
import user.peiso.com.au.ui.dialog.CommonDialog

/**
 * Created by forever_zero on 2022/12/27.
 **/

fun BaseViewModel.httpSendCode(
    mobile: String,
    callback: (String?) -> Unit){
    APIRequest<String?>(this)
        .loading("send sms")
        .request {
            RetrofitService.instant().httpSendCode(mobile)
        }
        .registerSucceed {
            callback.invoke(it)
        }
        .start()
}

fun BaseViewModel.httpCheckHasPassword(mobile: String,
                                       callback: (Boolean) -> Unit){
    APIRequest<Boolean>(this)
        .loading("check has password")
        .request {
            val map = hashMapOf("mobileNumber" to mobile)
            RetrofitService.instant().httpCheckHasPassword(map)
        }
        .registerSucceed {
            callback.invoke(it == true)
        }
        .registerFailure {
            callback.invoke(true)
        }
        .start()
}

fun BaseViewModel.httpTodaySummary(id: String,
                                   isUseLoading: Boolean,
                                   callback: (TodaySummaryEntity) -> Unit){
    APIRequest<TodaySummaryEntity>(this)
        .loading(if(isUseLoading) "summary" else APIRequest.REQUEST_LOADING_NOT)
        .request {
            RetrofitService.instant().httpTodaySummary(id)
        }
        .registerSucceed {
            it?.also(callback)
        }
        .start()
}

fun BaseViewModel.httpGetTodayAllData(id: String,
                                      isUseLoading: Boolean = true,
                                      finish: (() -> Unit)?,
                                      callback: (TodayDataEntity) -> Unit){
    APIRequest<TodayDataEntity>(this)
        .loading(if(isUseLoading) "summary" else APIRequest.REQUEST_LOADING_NOT)
        .request {
            RetrofitService.instant().httpGetTodayAllData(id)
        }
        .registerSucceed {
            it?.also(callback)
        }
        .registerFinish {
            finish?.invoke()
        }
        .start()
}


fun BaseViewModel.httpGetTodayAllDataMinutes(id: String,
                                             isUseLoading: Boolean = true,
                                             finish: (() -> Unit)?,
                                             callback: (TodayDataEntity) -> Unit){
    APIRequest<TodayDataEntity>(this)
        .loading(if(isUseLoading) "daily" else APIRequest.REQUEST_LOADING_NOT)
        .request {
            RetrofitService.instant().httpGetTodayAllDataMinutes(id)
        }
        .registerSucceed {
            it?.also(callback)
        }
        .registerFinish {
            finish?.invoke()
        }
        .start()
}

fun BaseViewModel.httpGetDaily(id: String,
                               isNeedLoading: Boolean = true,
                               error: (() -> Unit) ?= null,
                               callback: (DailyEntity) -> Unit){
    APIRequest<DailyEntity>(this)
        .loading(if(isNeedLoading) "daily" else APIRequest.REQUEST_LOADING_NOT)
        .request {
            RetrofitService.instant().httpGetDaily(id)
        }
        .registerSucceed {
            it?.also(callback)
        }
        .registerFailure {
            error?.invoke()
        }
        .start()
}


fun BaseViewModel.httpGetWeekList(id: String,
                                  isNeedLoading: Boolean = true,
                                  error: (() -> Unit) ?= null,
                                  finish: (() -> Unit) ?= null,
                                  callback: (List<WeekItemEntity>) -> Unit){
    APIRequest<List<WeekItemEntity>>(this)
        .loading(if(isNeedLoading) "week list" else APIRequest.REQUEST_LOADING_NOT)
        .request {
            RetrofitService.instant().httpGetWeekList(id)
        }
        .registerSucceed {
            it?.also(callback)
        }
        .registerFailure{
            error?.invoke()
        }
        .registerFinish {
            finish?.invoke()
        }
        .start()
}

fun BaseViewModel.httpGetWeek(venueId: String,
                              isNeedLoading: Boolean = true,
                              finish: (() -> Unit)? = null,
                              callback: (WeekDetailsEntity) -> Unit){
    APIRequest<WeekDetailsEntity>(this)
        .loading(if(isNeedLoading) "daily" else APIRequest.REQUEST_LOADING_NOT)
        .request {
            RetrofitService.instant().httpGetWeek(venueId)
        }
        .registerSucceed {
            it?.also(callback)
        }
        .registerFinish{
            finish?.invoke()
        }
        .start()
}

fun BaseViewModel.httpCheckUpdate(callback: (UpdateEntity) -> Unit){
    APIRequest<UpdateEntity>(this)
        .request {
            val map = hashMapOf("platform" to "1", "versionCode" to BuildConfig.VERSION_NAME)
            RetrofitService.instant().httpCheckUpdate(map)
        }
        .registerSucceed {
            it?.also { callback.invoke(it) }
        }
        .start()
}