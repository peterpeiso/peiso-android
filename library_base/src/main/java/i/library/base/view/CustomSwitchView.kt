package i.library.base.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.graphics.alpha
import i.library.base.R
import i.library.base.expands.logo
import i.library.base.utils.IColorUtils

/**
 * Created by hc. on 2021/12/1
 * Describe: Switch
 */
class CustomSwitchView(context: Context, attrs: AttributeSet) : View(context,attrs){

    private var iLineWidth = 5f
    private var iLineShadow = 12

    private var iColorBGOpen = Color.parseColor("#8CDE76")
    private var iColorBGClose = Color.parseColor("#FFFFFF")
    private var iColorCircle = Color.parseColor("#FFFFFF")
    private var iColorLine = Color.parseColor("#CCCCCC")
    
    private var iColorLineOpen = iColorLine
    private var iColorCircleOpen = iColorCircle

    private var iCircleMargin = 5f

    private var isInitMeasure = false

    private var isUnderway = false

    private var iMarginBackgroundHeight = 0f
    private var iMarginBackgroundWidth = 0f

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomSwitchView)
        iColorLine = typedArray.getColor(R.styleable.CustomSwitchView_colorLine,iColorLine)
        iColorBGOpen = typedArray.getColor(R.styleable.CustomSwitchView_colorOpen,iColorBGOpen)
        iColorBGClose = typedArray.getColor(R.styleable.CustomSwitchView_colorClose,iColorBGClose)
        iColorCircle = typedArray.getColor(R.styleable.CustomSwitchView_colorCircle,iColorCircle)
        iLineWidth = typedArray.getDimension(R.styleable.CustomSwitchView_lineWidth,iLineWidth)
        iCircleMargin = typedArray.getDimension(R.styleable.CustomSwitchView_circleMargin,iCircleMargin)
        iColorLineOpen = typedArray.getColor(R.styleable.CustomSwitchView_colorLineOpen,iColorLine)
        iColorCircleOpen = typedArray.getColor(R.styleable.CustomSwitchView_colorCircleOpen,iColorCircle)
        iMarginBackgroundHeight = typedArray.getDimension(R.styleable.CustomSwitchView_marginBgHeight,iMarginBackgroundHeight)
        iMarginBackgroundWidth = typedArray.getDimension(R.styleable.CustomSwitchView_marginBgWidth,iMarginBackgroundWidth)
        typedArray.recycle()
    }

    private val iPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color =  Color.WHITE
    }

    private val iColorPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = iColorBGOpen
    }

    private val iLinePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = iLineWidth * 1f
        color = iColorLine
    }

    private var iViewWidth: Int = 0
    private var iViewHeight: Int = 0
    private var iAllHeight: Int = 0
    private var iAllWidth: Int = 0
    private var iCircleRadius = 0f

    private var iSwitchState = false

    private var iPosition = 0f
    private var iPositionStart = 0f
    private var iPositionEnd = 0f

    private var iRoundRect: RectF = RectF()

    var iStateChangeBack : ((state: Boolean) -> Unit) ?= null

    fun getThisState(): Boolean{
        return iSwitchState
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        /* progress */
        val progress =( iPosition - iPositionStart ) / (iPositionEnd - iPositionStart)
        /* background */
        iPaint.color = iColorBGClose
        iPaint.setShadowLayer(iLineShadow / 2f,0f,0f,Color.GRAY)
        val radius = iViewHeight / 2f
        canvas.drawRoundRect(iRoundRect,radius,radius,iPaint)
        /* line */
        iLinePaint.color = if(iColorLine != iColorLineOpen){
            IColorUtils.getCurrentColor(progress,iColorLine,iColorLineOpen)
        }else{
            iColorLine
        }
        canvas.drawRoundRect(iRoundRect,radius,radius,iLinePaint)
        /* color */
        val alphaOpen = iColorBGOpen.alpha
        val alpha = (255 * (alphaOpen / 255f)) * progress
        iColorPaint.alpha = alpha.toInt()
        canvas.drawRoundRect(iRoundRect,radius,radius,iColorPaint)
        /* circle */
        iPaint.color = if(iColorCircle != iColorCircleOpen){
            IColorUtils.getCurrentColor(progress,iColorCircle,iColorCircleOpen)
        }else{
            iColorCircle
        }
        iPaint.setShadowLayer(iLineShadow * 1f,0f,0f,Color.GRAY)
        canvas.drawCircle(iPosition,iAllHeight / 2f,iCircleRadius,iPaint)
        canvas.drawCircle(iPosition,iAllHeight / 2f,iCircleRadius,iLinePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_UP -> {
                if(!isUnderway){
                    inStateChange(!iSwitchState)
                }
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        "performClick".logo()
        return super.performClick()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        iAllHeight = MeasureSpec.getSize(heightMeasureSpec)
        iAllWidth = MeasureSpec.getSize(widthMeasureSpec)
        iLineShadow = iAllHeight / 10
        val lineWidth = iLineWidth + iLineShadow
        iViewHeight = (iAllHeight - lineWidth).toInt()
        iViewWidth = (iAllWidth - lineWidth).toInt()
        if(iCircleMargin == 5f){
            iCircleMargin = iViewHeight / 10f
        }
        iCircleRadius = iViewHeight / 2f  - iCircleMargin
        iPositionStart = iCircleRadius + iCircleMargin + lineWidth / 2f
        iPositionEnd = iAllWidth - iPositionStart
        iRoundRect.apply {
            top = lineWidth / 2f + iMarginBackgroundHeight
            bottom = iAllHeight - lineWidth / 2f - iMarginBackgroundHeight
            left = lineWidth / 2f + iMarginBackgroundWidth
            right = iAllWidth - lineWidth / 2f - iMarginBackgroundWidth
        }
        isInitMeasure = true
        iPosition = if(iSwitchState) iPositionEnd else iPositionStart
        invalidate()
    }

    private fun inStateChange(isOpen: Boolean,isBackChange : Boolean = true){
        iSwitchState = isOpen
        if(!isInitMeasure){
            if(isBackChange){
                iStateChangeBack?.invoke(iSwitchState)
            }
            return
        }
        isUnderway = true
        val toPosition = if(isOpen) iPositionEnd else iPositionStart
        ValueAnimator.ofFloat(iPosition,toPosition).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                iPosition = it.animatedValue as Float
                invalidate()
            }
            addListener(
                onEnd = {
                    isUnderway = false
                    if(isBackChange){
                        iStateChangeBack?.invoke(iSwitchState)
                    }
                },onCancel = {
                    isUnderway = false
                    if(isBackChange){
                        iStateChangeBack?.invoke(iSwitchState)
                    }
                }
            )
            start()
        }
    }

    fun setSwitchState(isOpen: Boolean,isBackChange : Boolean = false){
        inStateChange(isOpen,isBackChange)
    }

}