package i.library.base.base

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import i.library.base.constant.INFORM_LOAD_END
import i.library.base.constant.INFORM_LOAD_START
import i.library.base.entity.InformEntity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Created by hc. on 2021/11/8
 * Describe: BaseVModel
 */
abstract class BaseViewModel : ViewModel(){

    var cleareds: ArrayList<() -> Unit> = ArrayList()

    val application = BaseApplication.getInstance()

    lateinit var mLifecycle : LifecycleOwner

    val mInformLiveData : SingleLiveEvent<InformEntity> by lazy {
        SingleLiveEvent()
    }

    fun getString(id : Int): String {
        return application.getString(id)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return application.getString(resId, *formatArgs)
    }

    fun getColor(id : Int): Int{
        return ContextCompat.getColor(application,id)
    }

    fun loadVisibility(key: String, boolean: Boolean) {
        BaseApplication.toSendInform(if(boolean) INFORM_LOAD_START else INFORM_LOAD_END,key)
    }

    fun toInform(key : String,value : Any = ""){
        MainScope().launch {
            mInformLiveData.value =
                InformEntity(key, value)
        }
    }

    override fun onCleared() {
        cleareds.forEach {
            it.invoke()
        }
        super.onCleared()
    }
}