package user.peiso.com.au.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import i.library.base.utils.ScreenUtils.dpf

/**
 * Created by forever_zero on 2022/12/27.
 **/
class PagerIndicator(context: Context, attr: AttributeSet): View(context,attr) {

    private val paint by lazy {
        Paint().apply {
            strokeWidth = 1.dpf
            isAntiAlias = true
        }
    }

    private val size = 5.dpf

    private val margin = 20.dpf

    private var select = 0

    private var len = 3

    private val color = Color.parseColor("#EA3796")
    private val color2 = Color.parseColor("#80FFFFFF")

    fun iSetSelect(position: Int){
        select = position
        invalidate()
    }

    fun iSetLen(len: Int){
        this.len = len
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        val needWidth = size * len + margin * (len - 1)
        for (i in 0 until len){
            paint.color = if(select == i) color else color2
            paint.style = if(select == i) Paint.Style.FILL else Paint.Style.STROKE
            canvas.drawCircle(width / 2f - needWidth /2 + (size / 2) + (i * (size + margin)), height / 2f, size,paint)
        }
    }

}