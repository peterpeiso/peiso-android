package i.library.base.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import i.library.base.base.BaseApplication

/**
 * @date 2019/9/19.
 * @author t.
 * Description: Toast
 */

object AToastUtils {

    /** Toast 单例 **/
    private var mToast : Toast ?= null
    /** 每次show间隔不能低于4秒 **/
    private var mMinTime = 4000
    /** 上一次点击时间 **/
    private var mToastTime = 0L
    /** 上一次点击的Text **/
    private var mTextStr :String ?= null

    /** Toast show **/
    @SuppressLint("ShowToast")
    private fun showToast(context : Context, msg : String, time: Int){
        mTextStr = msg
        if(mToast == null){
            mToast = Toast.makeText(context,msg,time)
            mToast?.setGravity(Gravity.CENTER, 0, 0)
        }else{
            mToast?.setText(msg)
        }
        mToast?.show()
    }

    /** 外部调用 **/
    fun showToast(msg : String){
        if(System.currentTimeMillis() - mToastTime < mMinTime){
            if(mTextStr != msg){
                mToast?.setText(msg)
                mTextStr = msg
            }
            return
        }
        mToastTime = System.currentTimeMillis()
        showToast(
            BaseApplication.getInstance().applicationContext,
            msg,
            Toast.LENGTH_LONG
        )
    }

}