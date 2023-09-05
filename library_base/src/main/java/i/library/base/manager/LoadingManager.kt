package i.library.base.manager

import androidx.fragment.app.FragmentManager
import i.library.base.dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by hc. on 2021/11/8
 * Describe: 加载管理
 */
class LoadingManager {

    private val hashMap = HashMap<String,Int>()

    private var loading: LoadingDialogFragment ?= null

    private fun iGetLoading(): LoadingDialogFragment{
        return if(loading == null) {
            loading = LoadingDialogFragment().apply {
                setDismissBack {
                    hashMap.clear()
                }
            }
            loading!!
        }else loading!!
    }

    fun load(manager: FragmentManager,isShow: Boolean,key : String){
        if(isShow){
            manager.loadingStart(key)
        }else{
            /* 间隔一段时间之后再移除 */
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                manager.loadingFinish(key)
            }
        }
    }

    /* loading show */
    private fun FragmentManager.loadingStart(key : String){
        val count = hashMap[key]?:0
        hashMap[key] = count + 1
        refreshProgressState(this,true)
    }

    /* loading gone */
    private fun FragmentManager.loadingFinish(key : String){
        val count = hashMap[key]?:0
        if(count - 1 <= 0){
            hashMap.remove(key)
        }else{
            hashMap[key] = count - 1
        }
        if(hashMap.isEmpty()){
            refreshProgressState(this,false)
        }
    }

    private fun refreshProgressState(manager: FragmentManager,isShow: Boolean) {
        val isShowLoad = loading != null
        /* 移除Dialog */
        when{
            /* 已显示且需要显示 || 未显示且不需要显示 */
            (isShowLoad && isShow) || (!isShowLoad && !isShow )-> {}
            /* 已显示且不需要显示 */
            isShowLoad -> {
                manager.beginTransaction().remove(iGetLoading()).commit()
                loading = null
            }
            /* 显示Load... */
            else -> {
                iGetLoading().show(manager,"loading_manager")
            }
        }
    }
}