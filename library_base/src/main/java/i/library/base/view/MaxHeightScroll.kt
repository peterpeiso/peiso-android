package i.library.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import i.library.base.R
import i.library.base.utils.ScreenUtils


/**
 * Created by hc. on 2020/4/22
 * Describe:
 */
class MaxHeightScroll (context: Context, attrs: AttributeSet?) :
    NestedScrollView(context, attrs) {

    private var mMaxHeight = ScreenUtils.dip2px(420f)

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScroll)
        val maxHeightScroll = ScreenUtils.getScreenWidth(context) * 0.6f
        mMaxHeight =
            typedArray.getDimension(R.styleable.MaxHeightScroll_mMaxHeight,maxHeightScroll).toInt()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val mMaxHeight = mMaxHeight
        mSpecSize = if(mSpecSize > mMaxHeight){
            MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        }else{
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, mSpecSize)
    }

}