package i.library.base.base

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import i.library.base.entity.BackInformEntity
import i.library.base.more.AppUncaughtExceptionHandler
import i.library.base.entity.InformEntity
import i.library.base.listener.ActivityLifecycleListener
import i.library.base.listener.LoginManagerListener
import kotlinx.coroutines.*

/**
 * Created by hc. on 2021/11/5
 * Describe: Application
 */
abstract class BaseApplication : Application(){

    companion object{

        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

        private var instance : BaseApplication?= null

        fun getInstance() : BaseApplication {
            return instance!!
        }

        inline fun <reified T: BaseApplication> getInstanceT(): T{
            return getInstance() as T
        }

        fun toSendInform(key : String,value : Any = ""){
            CoroutineScope(Dispatchers.IO).launch {
                delay(200)
                MainScope().launch {
                    getInstance().iAPPInform.value = InformEntity(key,value)
                    getInstance().iAPPInform.value = null
                }
            }
        }

        fun iGetLoginManager(): LoginManagerListener {
            return getInstance().getLoginManager()
        }

        inline fun <reified T: LoginManagerListener> iGetLoginManagerT(): T {
            return getInstance().getLoginManager() as T
        }
    }

    private var mFactory: ViewModelProvider.Factory? = null

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }

    /** login manager **/
    abstract fun getLoginManager(): LoginManagerListener

    /** app inform **/
    val iAPPInform : MutableLiveData<InformEntity?> by lazy {
        val mutableLiveData = MutableLiveData<InformEntity?>()
        mutableLiveData.value = InformEntity("AppInit")
        mutableLiveData
    }

    /** app back **/
    val iBackInform: MutableLiveData<BackInformEntity> by lazy {
        val mutableLiveData = MutableLiveData<BackInformEntity>()
        mutableLiveData.value = BackInformEntity("",0)
        mutableLiveData
    }

    /** activity state **/
    private val mAppLifecycle = ActivityLifecycleListener()

    override fun onCreate() {
        super.onCreate()
        instance = this
        /* 注册页面周期管理 */
        registerActivityLifecycleCallbacks(mAppLifecycle)
        /* 初始化异常处理器 */
        AppUncaughtExceptionHandler().init()
    }

    fun iGetActiveCount() : Int{
        return mAppLifecycle.refCount
    }
}

