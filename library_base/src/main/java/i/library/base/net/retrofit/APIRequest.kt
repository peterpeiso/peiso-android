package i.library.base.net.retrofit

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import i.library.base.base.BaseApplication
import i.library.base.base.BaseViewModel
import i.library.base.constant.*
import i.library.base.entity.ApiResponse
import i.library.base.expands.logo
import i.library.base.expands.toast
import i.library.base.manager.ControlActivityManger
import i.library.base.net.HTTPCustomException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Created by hc. on 2021/11/10
 * Describe: Handling network requests
 */
class APIRequest<T>(private val model : BaseViewModel) {

    companion object{
        const val REQUEST_LOADING_NOT = "NOT"
    }

    private var loading = ""

    private var checkCode = true

    private lateinit var execute: suspend () -> ApiResponse<T>

    private var succeed:  ((entity : T?) -> Unit) ?= null

    private var succeedAll : ((entity : ApiResponse<T>?) -> Unit) ?= null

    private var error: ((request: ApiResponse<T> ) -> Unit) ?= null

    private var failure: ((throwable: Throwable) -> Unit) ?= null

    private var finish: (() -> Unit) ?= null

    private var isUseToast: Boolean = true

    fun loading(loading: String): APIRequest<T> {
        this.loading = if(TextUtils.isEmpty(loading)) "HTTP" else loading
        return this
    }

    fun request(execute: suspend () -> ApiResponse<T>): APIRequest<T> {
        this.execute = execute
        return this
    }

    fun registerError(error: (request: ApiResponse<T> ) -> Unit): APIRequest<T> {
        this.error = error
        return this
    }

    fun registerSucceed(succeed:  (entity : T?) -> Unit): APIRequest<T> {
        this.succeed = succeed
        return this
    }

    fun registerSucceedApi(succeed:  (entity : ApiResponse<T>?) -> Unit): APIRequest<T> {
        this.succeedAll = succeed
        return this
    }

    fun registerFailure(failure: (throwable: Throwable) -> Unit): APIRequest<T> {
        this.failure = failure
        return this
    }

    fun registerFinish(finish: () -> Unit): APIRequest<T> {
        this.finish = finish
        return this
    }

    fun toast(isUseToast: Boolean): APIRequest<T> {
        this.isUseToast = isUseToast
        return this
    }

    fun start(){
        loading(true)
        model.viewModelScope.launch(Dispatchers.IO){
            runCatching {
                val invoke = execute.invoke()
                val b = !checkCode(invoke)
                if(b || (checkCode && !invoke.isSucceed())){
                    if(error != null){
                        error!!.invoke(invoke)
                    }
                    if(b){
                        isUseToast = false
                        throw HTTPCustomException("")
                    }else{
                        throw HTTPCustomException(invoke.message)
                    }
                }
                invoke
            }.onSuccess {
                model.viewModelScope.launch(Dispatchers.Main){
                    val data = it.data
                    loading(false)
                    succeedAll?.invoke(it)
                    succeed?.invoke(data)
                    finish?.invoke()
                }
            }.onFailure {
                model.viewModelScope.launch(Dispatchers.Main) {
                    loading(false)
                    if (isUseToast) {
                        when(it){
                            is SocketTimeoutException -> {
                                "Timed out, please try again later."
                            }
                            is ConnectException -> {
                                "Connection failed, please try again later."
                            }
                            is NumberFormatException -> {
                                "Unexpected error: Parse exception."
                            }
                            is HTTPCustomException -> {
                                it.message
                            }
                            else -> {
                                "Connection failed."
                            }
                        }.toast()
                    }
                    failure?.invoke(it)
                    finish?.invoke()
                    /* output error */
                    it.printStackTrace()
                    "API_ERROR: ${it.message}".logo()
                }
            }
        }
    }

    private fun checkCode(response: ApiResponse<T>) : Boolean{
        var isSucceed = false
        when(response.code){
            CODE_TOKEN_NULL,
            CODE_TOKEN_ELSE,
            CODE_TOKEN_DELETE,
            CODE_TOKEN_EXPIRED,
            CODE_TOKEN_FORBIDDEN,
            CODE_TOKEN_INVALID-> {
                MainScope().launch {
                    loading(false)
                    BaseApplication.iGetLoginManager().iOutLogin(response.code)
                }
            }
            else -> isSucceed = true
        }
        return isSucceed
    }

    val clear = {
        BaseApplication.toSendInform(INFORM_LOAD_END,loading)
    }

    private fun loading(isStart : Boolean){
        if(TextUtils.isEmpty(loading) || loading == REQUEST_LOADING_NOT)return
        "loading: $loading.$isStart".logo()
        if(isStart){
            model.cleareds.add(clear)
        }else{
            model.cleareds.remove(clear)
        }
        BaseApplication.toSendInform(if(isStart) INFORM_LOAD_START else INFORM_LOAD_END,loading)
    }
}