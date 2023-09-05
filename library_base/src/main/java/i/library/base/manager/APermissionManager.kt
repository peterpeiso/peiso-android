package i.library.base.manager

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import i.library.base.expands.logo

/**
 * Created by hc. on 2020/6/2
 * Describe: 权限管理
 */
class APermissionManager(val activity : Activity) {

    private val requestPermissionBack : HashMap<Int,((isTrue : Boolean) -> Unit)> = HashMap()

    fun requestPermission(permissions : Array<String>,
                          requestCode : Int = 0,
                          back : ((isTrue : Boolean) -> Unit)){
        requestPermissionBack[requestCode] = back
        if(isHasPermission(permissions)){
            requestPermissionBack[requestCode]?.invoke(true)
        }else{
            ActivityCompat.requestPermissions(activity,permissions,requestCode)
        }
    }

    /** 是否都具有权限 **/
    fun isHasPermission(permissions : Array<String>) : Boolean{
        var isHas = true
        for(p in permissions){
            val isHasP =  isHasPermission(p)
            if(!isHasP){
                isHas = false
                break
            }
        }
        return isHas
    }

    /** 是否具有权限 **/
    private fun isHasPermission(permission : String) : Boolean{
        return ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED
    }

    fun clear(){
        requestPermissionBack.clear()
    }

    /** 处理权限回调 **/
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        "$permissions".logo()
        if (grantResults.isNotEmpty()) {
            var isAllHas = true
            for(i in grantResults){
                val isHasP = i == PackageManager.PERMISSION_GRANTED
                if(!isHasP){
                    isAllHas = false
                    break
                }
            }
            if(isAllHas){
                requestPermissionBack[requestCode]?.invoke(true)
            }else{
                requestPermissionBack[requestCode]?.invoke(false)
            }
        }else{
            requestPermissionBack[requestCode]?.invoke(false)
        }
    }
}