package i.library.base.view

import android.content.Context
import android.graphics.*
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import i.library.base.R
import i.library.base.utils.ScreenUtils


/**
 * Created by hc. on 2020/12/25
 * Describe: 固定数量的View
 */
class SmsNumberView(context: Context,attributeSet: AttributeSet)
    : AppCompatEditText(context,attributeSet) {

    /** 输入中颜色 **/
    private val mColorTyping  = Color.parseColor("#2B3444")

    /** 未输入颜色 **/
    private val mColorNotEntered = Color.parseColor("#2B3444")

    /** 输入后颜色 **/
    private val mColorEntered = Color.parseColor("#2B3444")

    /** 文字颜色 **/
    private val mTextColor = Color.parseColor("#FFFFFF")

    /** 最大输入数量 **/
    private var mMaxCount = 4

    /** 圆角 **/
    private val mRadius = ScreenUtils.dip2px(dpValue = 5f) * 1f

    /** 边框线宽度 **/
    private val mLineWidth = 5f

    /** 边框大小 **/
    private var mRectSize = ScreenUtils.dip2px(dpValue = 60f)

    /** 当前View宽度 **/
    private var mViewWidth = 0

    /** 当前View高度 **/
    private var mViewHeight = 0

    /** 按钮之间的间隔 **/
    private var mInterval = 0

    /** 输入框 **/
    private val mRect = RectF()

    private var iType = 0

    /** 画笔 **/
    private val mPaint : Paint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.typeface = Typeface.DEFAULT_BOLD
        paint
    }

    /* 输入完成 */
    var mEditInputOver : ((isOver : Boolean ,code :String) -> Unit) ?= null

    /** 初始化 **/
    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.SmsNumberView).apply{
            mMaxCount = getInteger(R.styleable.SmsNumberView_maxLen, mMaxCount)
        }.recycle()
        setMaxLength()
        isLongClickable = false
        isCursorVisible = false
        setTextColor(Color.TRANSPARENT)
        setHintTextColor(Color.TRANSPARENT)
        setBackgroundColor(Color.TRANSPARENT)
        addTextChangedListener(afterTextChanged = {
            val code = it?.toString()?:""
            val isOver = code.length == mMaxCount
            mEditInputOver?.invoke(isOver,code)
        })
    }

    /** 画视图 **/
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        setTextColor(Color.TRANSPARENT)
        setHintTextColor(Color.TRANSPARENT)
        drawRectBackground(canvas)
        drawText(canvas)
    }

    /** 画方框 **/
    private fun drawRectBackground(canvas: Canvas){
        canvas.translate(mLineWidth / 2, mLineWidth / 2)
        val count = canvas.saveCount
        canvas.save()
        mPaint.strokeWidth = mLineWidth
        mPaint.style = Paint.Style.FILL
        for(i in  0 until mMaxCount){
            val length = text.toString().length
            when{
                length == i -> {mPaint.color = mColorTyping}
                length <  i -> {mPaint.color = mColorNotEntered}
                length >  i -> {mPaint.color = mColorEntered}
                length == mMaxCount -> {mPaint.color = mColorEntered}
            }
            /* 画矩形 */
            if(iType == 0){
                canvas.drawRoundRect(mRect,mRadius,mRadius,mPaint)
            }else if(iType == 1){
                canvas.drawLine(mRect.left,mRect.bottom,mRect.right,mRect.bottom,mPaint)
            }
            /* 确定下一个方框的位置 */
            val dx: Float = mRect.right + mInterval * 1f
            canvas.save()
            canvas.translate(dx, 0f)
        }
        canvas.restoreToCount(count)
        canvas.translate(mLineWidth / 2, mLineWidth / 2)
    }

    /** 画文字 **/
    private fun drawText(canvas: Canvas){
        val count = canvas.saveCount
        canvas.translate(mLineWidth / 2, mLineWidth / 2)
        val length = editableText.length
        /* 设置画笔 */
        mPaint.style = Paint.Style.FILL
        mPaint.textSize = ScreenUtils.sp2px(context,24f) * 1f
        mPaint.color = mTextColor
        /* 文字 */
        for (i in 0 until length) {
            val inputType = inputType
            val text = if(inputType !=
                InputType.TYPE_NUMBER_VARIATION_PASSWORD or InputType.TYPE_CLASS_NUMBER)
                editableText[i].toString() else "*"
            val rect = Rect()
            mPaint.getTextBounds(text, 0, text.length, rect)
            val width = rect.width()
            val height = rect.height()
            val x = 1f * (mRectSize + mInterval) * i +
                    (mRectSize - mLineWidth) / 2 -
                    ScreenUtils.dip2px(dpValue = 3f) - width / 2
            val y = mRect.height() / 2 + height / 2f
            canvas.drawText(text, x, y, mPaint)
        }
        canvas.restoreToCount(count)
    }

    /** 计算宽高 **/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /*当前输入框的宽高信息 */
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec)
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec)
        if(mViewHeight < mRectSize ){
            mRectSize = mViewHeight
        }
        /** 间隔 **/
        val interval = (mViewWidth - (mRectSize * mMaxCount)) / (mMaxCount - 1)
        mInterval = if(interval > 10){
            interval
        }else{
            10
        }
        /** 方框 **/
        mRect.left = 0f
        mRect.top = 0f
        mRect.right = mRectSize * 1f - (mLineWidth / (mMaxCount - 1))
        mRect.bottom = mRectSize * 1f - mLineWidth
    }

    /** 不可复制 **/
    override fun onTextContextMenuItem(id: Int): Boolean { return false }

    /**
     * 设置最大长度
     */
    private fun setMaxLength(maxLength: Int = mMaxCount) {
        filters = if (maxLength >= 0) {
            arrayOf<InputFilter>(LengthFilter(maxLength))
        } else {
            arrayOfNulls(0)
        }
    }
}