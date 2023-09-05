package i.library.base.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import kotlin.math.abs

/**
 * Created by hc. on 2020/12/24
 * Describe: 滑动控制View
 */
class CustomScrollControlView(context: Context,attributeSet: AttributeSet)
    : HorizontalScrollView(context,attributeSet) {

    init {
        isHorizontalScrollBarEnabled = false
    }

    /** 是否打开控制器 **/
    private var isOpenControl = false

    /** 控制器宽度 **/
    private var mControlWidth = 0

    /** 开启和关闭的通知 **/
    private var mControlStateListener : ControlStateListener?= null

    fun setControlStateListener(listener : ControlStateListener){
        mControlStateListener = listener
    }

    interface ControlStateListener{
        fun onControlStateChange(view : CustomScrollControlView, isOpen : Boolean)
    }

    /** 处理滑动事件 **/
    var oldX  = 0f
    var oldY  = 0f
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN -> {
                oldX = ev.x
                oldY = ev.y
            }
            MotionEvent.ACTION_MOVE -> { }
            MotionEvent.ACTION_UP -> {
                val childAtWidth =  mControlWidth
                val openWidth = childAtWidth / 3
                val scrX = scrollX
                if(abs(oldX - ev.x) > 20 || abs(oldY - ev.y) > 20){
                    if(abs(scrX) > 20){
                        if(scrX > openWidth){
                            post {
                                isOpenControl = true
                                smoothScrollTo(childAtWidth,0)
                                mControlStateListener?.onControlStateChange(this,true)
                            }
                        }else{
                            post {
                                closeControl()
                            }
                        }
                    }else{
                        if(isOpenControl){
                            smoothScrollTo(0, 0)
                        }
                    }
                }else{
                    performClick()
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    fun closeControl(){
        isOpenControl = false
        smoothScrollTo(0, 0)
        mControlStateListener?.onControlStateChange(this,false)
    }

    /** 重写点击事件 **/
    override fun performClick(): Boolean {
        Log.d("View","PerformClick")
        return super.performClick()
    }

    /** 绘制View **/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val childAt = (getChildAt(0) as ViewGroup).getChildAt(0)
        val groupLayoutParams = childAt.layoutParams
        val margin = childAt.marginStart + childAt.marginEnd
        groupLayoutParams.width = widthSize - margin
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        postDelayed({
            val controlView = (getChildAt(0) as ViewGroup).getChildAt(1)
            mControlWidth = controlView.width
            mControlWidth += (controlView.layoutParams as LinearLayout.LayoutParams).marginStart
            mControlWidth += (controlView.layoutParams as LinearLayout.LayoutParams).marginEnd
        },100)
    }

}