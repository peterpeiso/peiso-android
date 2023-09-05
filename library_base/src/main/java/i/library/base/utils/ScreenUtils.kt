package i.library.base.utils

import android.app.Activity
import android.content.Context

import i.library.base.base.BaseFragment
import android.content.res.Resources


/**
 * Created by Administrator
 * Data: 2019/8/15.
 * Intro: - 屏幕适配
 */

object ScreenUtils {

    val Number.dpf: Float
        get() = dip2pxFloat(this.toFloat())

    val Number.dip: Int
        get() = dip2pxFloat(this.toFloat()).toInt()

    private const val mScreenDp = 375f
    private var mDensity = 0f
    private var mScaledDensity = 0f
    private var mAppDensity = 0f

    fun setCustomDensity(activity: Activity) {
        val displayMetrics = activity.resources.displayMetrics
        if (mDensity == 0f) {
            mDensity = displayMetrics.density
            mScaledDensity = displayMetrics.scaledDensity
        }
        val targetDensity = if(mAppDensity == 0f) displayMetrics.widthPixels / mScreenDp else mAppDensity
        val targetDensityDpi = (160 * targetDensity).toInt()
        displayMetrics.density = targetDensity
        displayMetrics.scaledDensity = targetDensity
        displayMetrics.densityDpi = targetDensityDpi
        if(mAppDensity == 0f){
            mAppDensity = targetDensity
        }
    }

    fun refreshSize(fragment: BaseFragment<*,*>){
        if(mAppDensity == 0f)return
        val displayMetrics = fragment.requireActivity().resources.displayMetrics
        displayMetrics.density = mAppDensity
        displayMetrics.scaledDensity = mAppDensity
        displayMetrics.densityDpi = (160 * mAppDensity).toInt()
    }

    fun dip2px(dpValue: Float): Int {
        return (dpValue * mAppDensity + 0.5f).toInt()
    }


    fun dip2pxFloat(dpValue: Float,isWidth: Boolean = true): Float {
        return dpValue * mAppDensity + 0.5f
    }

    fun px2dip(pxValue: Float): Float {
        return pxValue / mAppDensity + 0.5f
    }

    fun resetAppOrientation(activity : Activity) {
        val displayMetrics = activity.resources.displayMetrics
        displayMetrics.density = mDensity
        displayMetrics.scaledDensity = mScaledDensity
        displayMetrics.densityDpi = (mDensity * 160).toInt()
    }

    fun getScreenWidth(context: Context,isWidth : Boolean = false) : Int{
        val displayMetrics = context.resources.displayMetrics
        return if(isWidth){
            displayMetrics.widthPixels
        }else{
            displayMetrics.heightPixels
        }
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}
