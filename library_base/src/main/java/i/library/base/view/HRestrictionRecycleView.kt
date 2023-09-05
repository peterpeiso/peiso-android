package i.library.base.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import i.library.base.R
import i.library.base.utils.ScreenUtils

/**
 * @date 2019/10/28.
 * @author hc.
 * Description: Recycle 高度限制 - 300dp
 */
class HRestrictionRecycleView(context: Context, attrs: AttributeSet?) :
    RecyclerView(context, attrs) {

    var mMaxHeight = ScreenUtils.dip2px(dpValue = 360F) * 1f

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.HRestrictionRecycleView)
        mMaxHeight =
            typedArray.getDimension(R.styleable.HRestrictionRecycleView_maxRecycleHeight,mMaxHeight)
        typedArray.recycle()
    }

    override fun onMeasure(widthSpec: Int,heightSpec: Int) {
        var mSpecSize = MeasureSpec.getSize(heightSpec)
        val mMaxHeight = mMaxHeight.toInt()
        mSpecSize = if(mSpecSize > mMaxHeight){
           MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        }else{
            heightSpec
        }
        super.onMeasure(widthSpec, mSpecSize)
    }

}