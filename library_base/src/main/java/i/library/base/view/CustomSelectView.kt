package i.library.base.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import i.library.base.expands.logo
import i.library.base.utils.ScreenUtils
import kotlin.math.abs

/**
 * Created by hc. on 2021/12/21
 * Describe: select item
 */
class CustomSelectView: View {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context,attrs)

    companion object{
        const val ARRIVE_TOP = 1
        const val ARRIVE_BOTTOM = -1
        const val ARRIVE_CONTENT = 0
    }

    /* initOver */
    private var isInitOver = false

    private var isNeedBack = true

    /* position */
    private var position = 0
    private var currentPosition = 0f

    /* size */
    private var iViewWidth  = 0
    private var iViewHeight = 0

    /* text size */
    private var iTextSize = ScreenUtils.dip2px(24f) * 1f

    /* item height */
    private var iItemHeight = ScreenUtils.dip2px(40f)

    /* text color */
    private var iTextColor = Color.parseColor("#FFFFFF")

    /* text color -  */
    private var iTextUnSelectColor = Color.parseColor("#EDEDED")

    var itemChangeBack: ((item: CustomSelectDataInterface) -> Unit) ?= null

    fun setting(textSize: Float, itemHeight: Float, defaultColor: Int, selectColor: Int){
        iTextSize = ScreenUtils.dip2px(textSize) * 1f
        iItemHeight = ScreenUtils.dip2px(itemHeight)
        iTextColor = selectColor
        iTextUnSelectColor = defaultColor
    }

    /* text Paint */
    private val iTextPaint = Paint().apply {
        color = iTextColor
        textSize = iTextSize
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    /* data */
    private val data = ArrayList<CustomSelectDataInterface>()

    /* Velocity */
    private val iVelocityTracker by lazy {
        VelocityTracker.obtain()
    }

    /* Executing */
    private var iExecutingAnimation : ValueAnimator ?= null

    /* last touch Y */
    private var touchY = 0f

    /* arrive */
    private var arriveBoundary = ARRIVE_TOP

    fun iGetArrive() : Int = arriveBoundary

    /* refresh data */
    fun refreshData(list: List<CustomSelectDataInterface>){
        isNeedBack = true
        data.clear()
        data.addAll(list)
        animationLast()
    }

    /* select */
    fun select(position : Int,animation: Boolean = true){
        if(animation){
            animationTo(position)
        }else{
            moveUi(position * 1f)
        }
    }

    /* select */
    fun selectName(name: String,animation: Boolean = true,isUseBack: Boolean = true){
        var position = 0
        for(i in data.indices){
            if(name == data[i].iGetName()){
                position = i
                break
            }
        }
        isNeedBack = isUseBack
        if(animation){
            animationTo(position)
        }else{
            moveUi(position * 1f)
            positionChange(currentPosition.toInt())
        }
    }

    /* select */
    fun select(select : CustomSelectDataInterface,animation: Boolean = true){
        var position = 0
        for(i in data.indices){
            if(select.iGetID() == data[i].iGetID()){
                position = i
                break
            }
        }
        if(animation){
            animationTo(position)
        }else{
            moveUi(position * 1f)
        }
    }

    /* current item */
    fun iGetCurrentItem(): CustomSelectDataInterface{
        return data[position]
    }

    /* measure */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        iViewWidth  = MeasureSpec.getSize(widthMeasureSpec)
        iViewHeight = MeasureSpec.getSize(heightMeasureSpec)
    }

    /* touch */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                isNeedBack = true
                iExecutingAnimation?.cancel()
                touchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val realMove = iGetConversionMove(touchY - event.y)
                moveUi(currentPosition + realMove)
                iVelocityTracker.addMovement(event)
                touchY = event.y
            }
            MotionEvent.ACTION_UP   -> {
                iVelocityTracker.computeCurrentVelocity(200)
                val move = iVelocityTracker.yVelocity
                iVelocityTracker.clear()
                animationMove(-iGetConversionMove(move))
            }
        }
        return true
    }

    /* dispatch */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(arriveBoundary == ARRIVE_TOP){
            parent.requestDisallowInterceptTouchEvent(false)
        }else{
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.dispatchTouchEvent(event)
    }

    /* move */
    private fun animationMove(realMove: Float){
        val end : (animator: Animator) -> Unit = {
            iExecutingAnimation = null
            animationLast()
        }
        iExecutingAnimation?.cancel()
        iExecutingAnimation = ValueAnimator
            .ofFloat(currentPosition,currentPosition + realMove)
            .apply {
                duration = 300 + abs(realMove * 20).toLong()
                "duration: $duration".logo()
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    val move = it.animatedValue as Float
                    moveUi(move)
                }
                addListener(
                    onEnd = end,
                    onCancel = {
                    iExecutingAnimation = null
                })
                start()
        }
    }

    /* last */
    private fun animationLast(isNeedBack: Boolean = true){
        val i  = currentPosition.toInt()
        val to = if(currentPosition - i > 0.5f) i + 1 else i
        if(isNeedBack){
            animationTo(to)
        }else{
            val realTo = when{
                to < 0 -> 0
                to > data.size - 1 -> data.size - 1
                else -> to
            }
            moveUi(realTo * 1f)
            positionChange(currentPosition.toInt())
        }
    }

    /* Go! */
    private fun animationTo(to: Int){
        val realTo = when{
            to < 0 -> 0
            to > data.size - 1 -> data.size - 1
            else -> to
        }
        iExecutingAnimation?.cancel()
        iExecutingAnimation = ValueAnimator.ofFloat(currentPosition,realTo * 1f).apply {
            duration = 100
            addUpdateListener {
                val fl = it.animatedValue as Float
                moveUi(fl)
            }
            addListener(
                onEnd    = {
                    positionChange(currentPosition.toInt())
                    iExecutingAnimation = null
                },
                onCancel = { iExecutingAnimation = null })
            start()
        }
    }

    private fun positionChange(position: Int){
        this.position = position
        if(isNeedBack && position < data.size){
            itemChangeBack?.invoke(data[position])
        }
    }

    /* real move distance */
    private fun iGetConversionMove(move: Float): Float {
        return move / iItemHeight
    }

    /* move */
    private fun moveUi(toPosition: Float){
        currentPosition = when{
            toPosition <= 0f -> {
                arriveBoundary = ARRIVE_TOP
                0f
            }
            toPosition >= data.size - 1 ->{
                arriveBoundary = ARRIVE_BOTTOM
                data.size - 1f
            }
            else -> {
                arriveBoundary = ARRIVE_CONTENT
                toPosition
            }
        }
        invalidate()
    }

    val rect = Rect()
    /* draw */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        for(i in data.indices){
            val entity = data[i]
            val realPosition = i - currentPosition
            val realX = iViewWidth / 2f
            iTextPaint.color = if(realPosition < 0.5f && realPosition > -0.5f)
                iTextColor else iTextUnSelectColor
            iTextPaint.getTextBounds(entity.iGetName(),0,entity.iGetName().length,rect)
            val realY = iViewHeight / 2f + realPosition * iItemHeight + rect.height() / 2f
            canvas.drawText(entity.iGetName(),realX,realY,iTextPaint)
        }
    }//+top //-bottom /

    interface CustomSelectDataInterface{
        fun iGetID(): Long
        fun iGetName(): String
    }
}