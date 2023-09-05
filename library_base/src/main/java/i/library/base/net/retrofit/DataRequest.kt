package i.library.base.net.retrofit

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import i.library.base.base.BaseApplication
import i.library.base.base.BaseViewModel
import i.library.base.constant.*
import i.library.base.entity.ApiResponse
import i.library.base.expands.logo
import i.library.base.expands.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Created by hc. on 2021/11/10
 * Describe: Handling network requests
 */
class DataRequest<T>(private val model : BaseViewModel) {

    companion object{
        const val REQUEST_LOADING_NOT = "NOT"
    }

    private var loading = REQUEST_LOADING_NOT

    private lateinit var execute: suspend () -> T

    private var succeed:  ((entity : T) -> Unit) ?= null

    private var succeedAll : ((entity : T?) -> Unit) ?= null

    private var error: ((request: T ) -> Unit) ?= null

    private var failure: ((throwable: Throwable) -> Unit) ?= null

    private var finish: (() -> Unit) ?= null

    fun loading(loading: String): DataRequest<T> {
        this.loading = if(TextUtils.isEmpty(loading)) "HTTP" else loading
        return this
    }

    fun request(execute: suspend () -> T): DataRequest<T> {
        this.execute = execute
        return this
    }

    fun registerSucceed(succeed:  (entity : T) -> Unit): DataRequest<T> {
        this.succeed = succeed
        return this
    }

    fun start(){
        loading(true)
        model.viewModelScope.launch(Dispatchers.IO){
            runCatching {
                execute.invoke()
            }.onSuccess {
                model.viewModelScope.launch(Dispatchers.Main){
                    val data = it
                    loading(false)
                    succeed?.invoke(data)
                    succeedAll?.invoke(it)
                    finish?.invoke()
                }
            }.onFailure {
                model.viewModelScope.launch(Dispatchers.Main) {
                    loading(false)
                    if (error == null && !TextUtils.isEmpty(it.message)) {
                        it.message?.toast()
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

    private fun loading(isStart : Boolean){
        if(TextUtils.isEmpty(loading) || loading == REQUEST_LOADING_NOT)return
        model.loadVisibility(loading,isStart)
    }
}