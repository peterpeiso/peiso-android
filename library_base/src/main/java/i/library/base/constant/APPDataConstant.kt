package i.library.base.constant

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import i.library.base.base.BaseApplication
import i.library.base.base.BaseApplication.Companion.dataStore
import i.library.base.base.BaseViewModel
import i.library.base.constant.APPDataConstant.iGetData
import i.library.base.net.retrofit.DataRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Created by hc. on 2022/1/12
 * Describe:
 */
object APPDataConstant {

    /* user */
    val DATA_USER_INFO = stringPreferencesKey("user_info")
    val DATA_USER_TOKEN = stringPreferencesKey("user_token")

    fun Preferences.Key<String>.iGetData(succeed:(value: String) -> Unit){
        CoroutineScope(Dispatchers.IO).launch{
            delay(500)
            val value = BaseApplication.getInstance().dataStore.data.map {
                it[this@iGetData]?:""
            }.first()
            MainScope().launch {
                succeed.invoke(value)
            }
        }
    }

    fun Preferences.Key<String>.iSetData(value : String?) {
        CoroutineScope(Dispatchers.IO).launch{
            BaseApplication.getInstance().dataStore.edit {
                it[this@iSetData] = value?:""
            }
        }
    }
}