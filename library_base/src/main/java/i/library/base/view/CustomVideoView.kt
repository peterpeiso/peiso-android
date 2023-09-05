package i.library.base.view

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView(context: Context, attributeSet: AttributeSet)
    : VideoView(context, attributeSet){

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width=getDefaultSize(0,widthMeasureSpec)
        val height=getDefaultSize(0,heightMeasureSpec)
        setMeasuredDimension(width,height);
    }
}