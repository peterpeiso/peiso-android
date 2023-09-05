package i.library.base.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import i.library.base.base.BaseApplication

/**
 * Created by hc. on 2020/5/28
 * Describe:
 */
/** 创建普通圆角背景 **/
fun createGradientDrawable(@ColorInt color: Int, radiusPx : Float) : GradientDrawable{
    val colorDrawable =  GradientDrawable()
    colorDrawable.setColor(color)
    colorDrawable.cornerRadius = radiusPx
    return colorDrawable
}

/** 创建普通圆角背景 - 綫 **/
fun createGradientLineDrawable(@ColorInt color: Int,
                               radiusPx : Float,
                               line : Int = 3,
                               @ColorInt bgColor : Int = Color.TRANSPARENT)
        : GradientDrawable{
    val colorDrawable =  GradientDrawable()
    colorDrawable.setColor(bgColor)
    colorDrawable.setStroke(line,color)
    colorDrawable.cornerRadius = radiusPx
    return colorDrawable
}

/** 创建渐变圆角背景 **/
fun createGradientDrawable(colors : IntArray,radiusPx : Float): GradientDrawable{
    val colorDrawable =  GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,colors)
    colorDrawable.cornerRadius = radiusPx
    return colorDrawable
}

/** 创建渐变区分圆角背景 **/
fun createGradientRadiusDrawable(colors : IntArray,radiusPx : FloatArray): GradientDrawable{
    val colorDrawable =  GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,colors)
    colorDrawable.cornerRadii = radiusPx
    return colorDrawable
}

/** 创建渐变上下背景 **/
fun createGradient(colors : IntArray): GradientDrawable{
    val colorDrawable =  GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,colors)
    colorDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
    return colorDrawable
}

/** 显示与隐藏 **/
fun isUIVisibility(isVisibility : Boolean) : Int{
    return if(isVisibility){
        View.VISIBLE
    }else{
        View.GONE
    }
}


/** 获取布局VIEW **/
fun inflaterUIView(context : Context,id : Int) : View{
    val frameLayout = FrameLayout(context)
    frameLayout.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)
    return LayoutInflater.from(context).inflate(id,frameLayout,false)
}

/** 获取布局VIEW **/
fun<T : ViewDataBinding> inflaterBindingView(context : Context, id : Int) : T{
    val frameLayout = FrameLayout(context)
    frameLayout.layoutParams = RecyclerView.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)
    return DataBindingUtil.inflate(LayoutInflater.from(context), id,frameLayout,false)
}

/** View范围内? **/
fun isUiRangeOfView(view: View, ev: MotionEvent): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val x = location[0]
    val y = location[1]
    return !(ev.x < x || ev.x > x + view.width || ev.y < y || ev.y > y + view.height)
}