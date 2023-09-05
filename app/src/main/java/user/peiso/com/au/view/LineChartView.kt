package user.peiso.com.au.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import i.library.base.expands.logo
import i.library.base.utils.AMathUtils
import i.library.base.utils.ScreenUtils
import i.library.base.utils.ScreenUtils.dip
import i.library.base.utils.ScreenUtils.dpf
import java.lang.Float.max
import java.util.Calendar
import kotlin.math.abs

/**
 * Created by forever_zero on 2022/12/13.
 **/
class LineChartView(context: Context, attr: AttributeSet) : View(context, attr) {

    var chooses: ((List<LineNoteInterface>) -> Unit)? = null

    private var max = 0.0

    private val paint by lazy {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.strokeWidth = 1.dpf
        paint
    }

    private val linePaint by lazy {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.strokeWidth = 2.dpf
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.pathEffect = CornerPathEffect(2.dpf)
        paint
    }

    private val textPaint by lazy {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.textSize = ScreenUtils.sp2px(context, 10f) * 1f
        paint.textAlign = Paint.Align.LEFT
        paint
    }

    private val textRect = Rect(0, 0, 0, 0)

    private var iMaxValue = 0

    private var lines = ArrayList<LineCartData<*>>()

    private val margin = 20.dip

    private var paddingX = margin + 0.dpf

    private var isUseWeeks = false

    private var isUseHours = false

    fun clear(isUseMax: Boolean = false) {
        lines.clear()
        max = 0.0
        if(!isUseMax){
            iMaxValue = 0
        }
        iDocStart = 0f
    }

    fun finish(isUseWeek: Boolean,isUseHour: Boolean){
        isUseWeeks = isUseWeek
        isUseHours = isUseHour
        iGetCurrent()
    }

    fun addChart(color: Int, name: String, l: List<LineNoteInterface>): LineChartView {
        val list = ArrayList<LineNoteInterface>()
        list.addAll(l)
        val lineCartData = LineCartData(color, name, list)
        lines.add(lineCartData)
        list.forEach {
            if (it.iGetValue() > max) max = it.iGetValue()
        }
        /* refresh max */
        if (iMaxValue < max) {
            var k = 1
            for (i in 0 until "${max.toInt()}".length - 2) {
                k *= 10
            }
            val z = ((max / k).toInt() + 1)
            val zero = 5 //if(max.toInt() in 1000 .. 10000) 50 else
            ((if (z % zero == 0) z else z + (zero - z % zero)) * k)
            iMaxValue = when {
                z % zero == 0 -> z
                else -> z + (zero - z % zero)
            } * k
        }
        return this
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        val array = if(isUseWeeks) weeks else hours
        val iMaxValue = if (iMaxValue > 0) iMaxValue else if(isUseHours) 5 else 20000
        val iIntervalValue = iMaxValue / 5
        paint.style = Paint.Style.FILL
        val itemHeight = ( height - 40.dpf )/ 5
        val startY = 10.dpf
        /* text top */
        textPaint.textAlign = Paint.Align.LEFT
        val textH = textPaint.fontMetrics.let { font ->
            font.leading - font.top
        }
        /* frame */
        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.getTextBounds(iMaxValue.scaleK(), 0, iMaxValue.scaleK().length, textRect)
        val frameTextW = textRect.right - textRect.left * 1f
        var lastFrameTextY = startY
        textPaint.color = Color.parseColor("#7C8DB5")
        paint.color = Color.parseColor("#27292C")
        var lastLineY = 0f
        for (i in iMaxValue downTo 0 step iIntervalValue) {
            val scaleK = i.scaleK()
            canvas.drawText(
                scaleK,
                paddingX + frameTextW,
                lastFrameTextY + (textH / 2) + 1.dpf,
                textPaint
            )
            lastLineY = lastFrameTextY + (textH / 4)
            canvas.drawLine(
                paddingX + frameTextW + 4.dpf,
                lastFrameTextY + (textH / 4),
                width - (paddingX + margin),
                lastLineY,
                paint
            )
            lastFrameTextY += itemHeight
        }
        /* week */
        textPaint.getTextBounds(array[0], 0, array[0].length, textRect)
        val weekSTextW = textRect.right - textRect.left * 1f
        textPaint.getTextBounds(array[array.size - 1], 0, array[array.size - 1].length, textRect)
        val weekETextW = textRect.right - textRect.left * 1f
        var iWeekX = paddingX + frameTextW + (weekSTextW / 2)
        val interval = (width - iWeekX - (paddingX + margin) - (weekETextW / 2)) / (array.size - 1)
        textPaint.textAlign = Paint.Align.CENTER
        for (i in array.indices) {
            val start = paddingX + frameTextW + 4.dpf
            val chartInterval = (width - start - (paddingX + margin)) / (array.size - 1)
            val w = start + (chartInterval * i)
            if(isUseWeeks){
                canvas.drawText(array[i], w, lastFrameTextY - ((textH / 4) * 3) + 5.dpf, textPaint)
            }else if(i % 24 == 0){
                canvas.drawText(array[i], w, lastFrameTextY - ((textH / 4) * 3) + 5.dpf, textPaint)
            }
            iWeekX += interval
        }
        /* value */
        val chartY1 = startY + (textH / 4)
        val chartY2 = chartY1 + (itemHeight * 5)
        val chartHeight = chartY2 - chartY1
        /* lines */
        var iMAXEnd = 0f
        lines.forEach { line ->
            var start = paddingX + frameTextW + 4.dpf
            val chartInterval = (width - start - (paddingX + margin)) / (array.size - 1)
            if (iDocStart == 0f) {
                iDocStart = start
                iDocInterval = chartInterval
            }
            path.reset()
            var size = 0
            var lastDocX = 0f
            var lastDocY = 0f
            linePaint.color = line.color
            var iiSize = -1
            for (i in line.list.size - 1 downTo 0) {
                if (line.list[i].iGetValue() != -1.0) {
                    iiSize = i
                    break
                }
            }
            for (i in 0..iiSize) {
                val doc = line.list[i]
                val value = if (doc.iGetValue() != -1.0) {
                    doc.iGetValue()
                } else {
                    0.0
                }
                val p = value / iMaxValue
                val y = chartY1 + (chartHeight - (chartHeight * p).toFloat())
                if (size == 0) {
                    path.moveTo(start, y)
                } else {
                    path.lineTo(start, y)
                }
                lastDocX = start
                lastDocY = y
                size++
                start += chartInterval
            }
            iMAXEnd = max(iMAXEnd, start)
            if (size == 1) {
                canvas.drawCircle(lastDocX, lastDocY, 1.dpf, linePaint)
            } else {
                canvas.drawPath(path, linePaint)
            }
        }
        iDocEnd = width - (paddingX + margin)//iMAXEnd
        /* choose */
        if (iShowDayIncome >= 0 && iDocStart != 0f) {
            paint.color = Color.GREEN
            /* line */
            val x = iDocStart + (iDocInterval * iShowDayIncome)
            canvas.drawLine(x, chartY1, x, lastLineY, paint)
            refreshChoose()
        }
    }

    fun refreshChoose(){
        val chooseX = arrayListOf<LineNoteInterface>()
        for(i in lines){
            chooseX.add(i.list[iShowDayIncome])
        }
        "Choose: $iShowDayIncome".logo()
        chooses?.invoke(chooseX)
    }

    private val calendar by lazy {
        Calendar.getInstance()
    }

    private fun iGetCurrent(){
        calendar.timeInMillis = System.currentTimeMillis()
        val current =
            if (isUseWeeks){
                val day = calendar.get(Calendar.DAY_OF_WEEK)
                val d2 = if(day == 1) 7 else day - 1
                d2 - 1
            } else {
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                hour * 4 + minute / 15
            }
        iShowDayIncome = current
        invalidate()
    }

    private var iDocStart = 0f
    private var iDocEnd = 0f
    private var iDocInterval = 0f
    private var iShowDayIncome = -1
    private var downX = 0f
    private var downY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if(event.action == MotionEvent.ACTION_DOWN){
                    downX = event.x
                    downY = event.y
                }
                if(downX != 0f && abs(event.x - downX) > abs(event.y - downY)){
                    iGetRefresh(this)?.isEnabled = false
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                when{
                    event.x in iDocStart..iDocEnd -> {
                        val a = ((event.x - iDocStart + (iDocInterval / 2)) / iDocInterval).toInt()
                        if((isUseWeeks && a in weeks.indices) || (!isUseWeeks && a in hours.indices)){
                            iShowDayIncome = a
                        }
                    }
                    event.x >= iDocEnd
                            && (abs(event.x - iDocEnd) < 50 || downX in iDocStart..iDocEnd) -> {
                        iShowDayIncome = (if(isUseWeeks) weeks else hours).size - 1
                    }
                    event.x <= iDocStart
                            && (abs(event.x - iDocEnd) < 50 || downX in iDocStart..iDocEnd) -> {
                        iShowDayIncome = 0
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                iGetRefresh(this)?.isEnabled = true
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    private fun iGetRefresh(view: View, count: Int = 0): SwipeRefreshLayout? {
        val p = view.parent
        return if (p is SwipeRefreshLayout) {
            p
        } else if (p is View && count < 5) {
            iGetRefresh(p as View, count + 1)
        } else {
            null
        }
    }

    private fun Int.scaleK(): String {
        return when {
            this >= 1000 -> {
                "${AMathUtils.formatSize(this / 1000.0, scale = 1, isUseZero = false)}k"
            }
            else -> "$this"
        }
    }

    private val weeks = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val hours = Array(97){ "0" }.apply {
        for (i in indices){
            val value = i * 15
            val hour = value / 60
            set(i, "$hour")
        }
    }

    class LineCartData<T : LineNoteInterface>(val color: Int, val name: String, val list: List<T>)

    interface LineNoteInterface {

        fun iGetRealValue(): Double

        fun iGetValue(): Double

        fun iGetValueStr(): String
    }
}
