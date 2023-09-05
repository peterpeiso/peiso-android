package i.library.base.expands

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import i.library.base.BuildConfig
import i.library.base.R
import i.library.base.utils.AToastUtils

/**
 * Created by hc. on 2021/11/5
 * Describe: 常用的拓展方法
 */

/* 日志 */
fun String.logo(tag : String = "app.logo",level : Int = 3){
    if(!BuildConfig.DEBUG)return
    when(level){
        0 -> Log.i(tag,this)
        1 -> Log.d(tag,this)
        2 -> Log.w(tag,this)
        3 -> Log.e(tag,this)
    }
}

/* toast */
fun String?.toast(){
    this?:return
    AToastUtils.showToast(this)
}

/* navigation */
var NAV_LAST_START = 0L
var LAST_NAVIGATION = -1
fun NavController.navigationTo(id: Int,
                               args: Bundle?= null,
                               iNavOptions: NavOptions?= null,
                               extras: Navigator.Extras?= null){
    if(LAST_NAVIGATION == id && System.currentTimeMillis() - NAV_LAST_START < 300){
        "Invalid this time".logo()
        return
    }
    "navigationTo $id".logo("ToggleGraph")
    val options = iNavOptions
        ?: navOptions {
            anim {
                enter = R.anim.left_in
                exit = R.anim.right_out
                popEnter = R.anim.right_in
                popExit = R.anim.left_out
            }
        }
    navigate(id,args,options,extras)
    LAST_NAVIGATION = id
    NAV_LAST_START = System.currentTimeMillis()
}

fun String?.checkEmpty(): Boolean{
    return this == null || this.isEmpty()
}