package i.library.base.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * Created by hc. on 2021/12/15
 * Describe:
 */
object APKUtils {

    /** 跳转指定的应用市场更新 **/
    fun toMarketUpdate(activity: Activity){
        if(isHasAPP(activity)){
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                val uri: Uri = Uri.parse("market://details?id=" + activity.packageName)
                intent.data = uri
                intent.setPackage("com.android.vending")
                activity.startActivity(intent)
            }catch (e : ActivityNotFoundException){
                e.printStackTrace()
                val toUri = Uri.parse("https://play.google.com/store/apps/details?" +
                        "id=${activity.packageName}")
                val aIntent = Intent(Intent.ACTION_VIEW, toUri)
                activity.startActivity(aIntent)
            }
        }else{
            val toUri = Uri.parse("https://play.google.com/store/apps/details?" +
                    "id=${activity.packageName}")
            val aIntent = Intent(Intent.ACTION_VIEW, toUri)
            activity.startActivity(aIntent)
            return
        }
    }

    /** 判断是否安装APP **/
    private fun isHasAPP(activity: Activity,aPackage : String = "com.android.vending") : Boolean{
        val isHas: Boolean
        try {
            val packageInfo =
                activity.packageManager.getPackageInfo(aPackage, PackageManager.GET_GIDS)
            isHas = packageInfo != null
        }catch (e : Exception){
            return false
        }
        return isHas
    }
}