package i.library.base.manager

import i.library.base.base.BaseActivity

/**
 * Created by hc. on 2020/5/26
 * Describe: Activity 管理
 */
object ControlActivityManger {

    private val mActivityList = ArrayList<BaseActivity<*>>()

    fun getActivityList() : ArrayList<BaseActivity<*>>{
        return mActivityList
    }

    fun addActivity(activity: BaseActivity<*>){
        mActivityList.add(activity)
    }

    fun removeActivity(activity: BaseActivity<*>){
        mActivityList.remove(activity)
    }

    fun removeActivityTAG(clazz: Class<*>){
        mActivityList.forEach {
            if(it.javaClass == clazz){
                it.finish()
            }
        }
    }

    /** 除了此Activity之外的所有Activity全部关闭 */
    fun finishExcept(exceptClass: Class<out BaseActivity<*>>) {
        var arrayList: ArrayList<BaseActivity<*>>
        synchronized(mActivityList) {
            arrayList = ArrayList(mActivityList)
        }
        for (activity in arrayList) {
            if (activity.javaClass != exceptClass) {
                activity.finish()
            }
        }
    }

    /** 除了此Activity之外的所有Activity全部关闭 */
    fun finishExcepts(array : Array<String>) {
        var arrayList: ArrayList<BaseActivity<*>>
        synchronized(mActivityList) {
            arrayList = ArrayList(mActivityList)
        }
        for (activity in arrayList) {
            var isFinish = true
            array.forEach {
                if (activity.javaClass.name == it) {
                    isFinish = true
                    return@forEach
                }
            }
            if (isFinish) {
                activity.finish()
            }
        }
    }

    /** 获取前台的Activity **/
    fun getCurrentActivity() : BaseActivity<*>?{
        var arrayList: ArrayList<BaseActivity<*>>
        synchronized(mActivityList) {
            arrayList = ArrayList(mActivityList)
        }
        return if(arrayList.isNotEmpty()){
            arrayList[arrayList.size - 1]
        }else{
            null
        }
    }

    /** 获取前台的Activity **/
    fun getCurrentActivity(index: Int) : BaseActivity<*>?{
        var arrayList: ArrayList<BaseActivity<*>>
        synchronized(mActivityList) {
            arrayList = ArrayList(mActivityList)
        }
        return if(arrayList.isNotEmpty() && arrayList.size >= index + 1){
            arrayList[arrayList.size - 1 - index]
        }else{
            null
        }
    }

}