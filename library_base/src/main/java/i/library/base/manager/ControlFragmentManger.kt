package i.library.base.manager

import i.library.base.base.BaseFragment
import i.library.base.expands.logo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by hc. on 2021/12/29
 * Describe:
 */
object ControlFragmentManger {

    private val mActivityList = ArrayList<BaseFragment<*,*>>()

    fun getActivityList() : ArrayList<BaseFragment<*,*>>{
        return mActivityList
    }

    fun addFragment(fragment: BaseFragment<*,*>){
        mActivityList.add(fragment)
    }

    fun removeFragment(fragment: BaseFragment<*,*>){
        mActivityList.remove(fragment)
    }

    /** 获取前台的Fragment **/
    fun getCurrentFragment() : BaseFragment<*,*>?{
        var arrayList: ArrayList<BaseFragment<*,*>>
        synchronized(mActivityList) {
            arrayList = ArrayList(mActivityList)
        }
        return if(arrayList.isNotEmpty()){
            arrayList[arrayList.size - 1]
        }else{
            null
        }
    }


}