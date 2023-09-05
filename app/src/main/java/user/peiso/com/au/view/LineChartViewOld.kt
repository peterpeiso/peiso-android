package user.peiso.com.au.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import i.library.base.utils.AMathUtils
import i.library.base.utils.ScreenUtils
import i.library.base.utils.ScreenUtils.dip
import i.library.base.utils.ScreenUtils.dpf
import user.peiso.com.au.R
import java.lang.Float.max

/**
 * Created by forever_zero on 2022/12/13.
 **/
class LineChartViewOld(context: Context, attr: AttributeSet) : View(context, attr) {

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

    private var iIntervalValue = iMaxValue / 5

    private var lines = ArrayList<LineCartData<*>>()

    private val margin = 20.dip

    private var paddingY = 20.dpf

    private var paddingX = margin + 20.dpf

    private var indicatorSize = 4.dpf

    fun clear() {
        lines.clear()
        max = 0.0
        iMaxValue = 0
    }

    fun addChart(color: Int, name: String, l: List<LineNoteInterface>) {
        val list = ArrayList<LineNoteInterface>()
        if (l.size > 7) {
            for (i in 0..6) {
                list.add(l[i])
            }
        } else {
            list.addAll(l)
        }
        val lineCartData = LineCartData(color, name, list)
        lines.add(lineCartData)
        invalidate()
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
            val zero = 5
            iMaxValue = ((if (z % zero == 0) z else z + (zero - z % zero)) * k)
            iIntervalValue = iMaxValue / 5
        }
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        if (iMaxValue == 0) return
        paint.style = Paint.Style.FILL
        /* background */
        paint.color = Color.parseColor("#151C27")
        canvas.drawRoundRect(0f + margin, 0 * 1f, width * 1f - margin * 2, height * 1f, 10.dpf, 10.dpf, paint)
        /* text top */
        textPaint.textAlign = Paint.Align.LEFT
        var lastTextX = paddingX
        var textH = textPaint.fontMetrics.let { font ->
            font.leading - font.top
        }
        lines.forEach {
            val text = it.name
            /* text */
            textPaint.getTextBounds(text, 0, text.length, textRect)
            val textW = textRect.right - textRect.left * 1f
            /* doc */
            paint.color = it.color
            canvas.drawCircle(lastTextX + indicatorSize, paddingY + (textH / 4), indicatorSize, paint)
            /* text */
            textPaint.color = Color.WHITE
            paint.color = it.color
            val y = paddingY + (textH / 2) + 1.dpf
            canvas.drawText(text, lastTextX + (indicatorSize * 2f) + 5.dip, y, textPaint)
            lastTextX += (indicatorSize * 2f) + 5.dip + textW + 20.dpf
        }
        textH = (height - 108.dpf) / 7
        /* frame */
        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.getTextBounds(iMaxValue.scaleK(), 0, iMaxValue.scaleK().length, textRect)
        val frameTextW = textRect.right - textRect.left * 1f
        var lastFrameTextY = 48.dpf
        textPaint.color = Color.parseColor("#7C8DB5")
        paint.color = Color.parseColor("#27292C")
        var lastLineY = 0f
        for (i in iMaxValue downTo 0 step iIntervalValue) {
            canvas.drawText(
                i.scaleK(),
                paddingX + frameTextW,
                lastFrameTextY + (textH / 2) - 2.dpf,
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
            lastFrameTextY += textH + 10.dpf
        }
        /* week */
        textPaint.getTextBounds(weeks[0], 0, weeks[0].length, textRect)
        val weekSTextW = textRect.right - textRect.left * 1f
        textPaint.getTextBounds(weeks[weeks.size - 1], 0, weeks[weeks.size - 1].length, textRect)
        val weekETextW = textRect.right - textRect.left * 1f
        var iWeekX = paddingX + frameTextW + (weekSTextW / 2)
        val interval = (width - iWeekX - (paddingX + margin) - (weekETextW / 2)) / (weeks.size - 1)
        textPaint.textAlign = Paint.Align.CENTER
        for (i in weeks.indices) {
            canvas.drawText(weeks[i], iWeekX, lastFrameTextY - ((textH / 4) * 3) + 10.dpf, textPaint)
            iWeekX += interval
        }
        /* value */
        val chartY1 = 48.dpf + (textH / 4)
        val chartY2 = chartY1 + ((textH + 10.dpf) * 5)
        val chartHeight = chartY2 - chartY1
        /* lines */
        var iMAXEnd = 0f
        lines.forEach { line ->
            var start = paddingX + frameTextW + 4.dpf
            val chartInterval = (width - start - (paddingX + margin)) / (weeks.size - 1)
            if (iDocStart == 0f) {
                iDocStart = start
                iDocInterval = chartInterval
            }
            path.reset()
            var size = 0
            var lastDocX = 0f
            var lastDocY = 0f
            linePaint.color = line.color
            var iiSize = 0
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
        iDocEnd = iMAXEnd
        /* choose */
        if (iShowDayIncome != -1) {
            var size = 0
            lines.forEach {
                if (it.list[iShowDayIncome].iGetValue() != -1.0) {
                    size += 1
                }
            }
            if (size == 0)return
            paint.color = Color.WHITE
            val widthG = 50.dpf
            /* line */
            val x = iDocStart + (iDocInterval * iShowDayIncome)
            canvas.drawLine(x, 48.dpf + (textH / 4), x, lastLineY, paint)
            /* background */
            val y = 40.dpf
            canvas.drawBitmap(bitmap, x - 7.dpf, y + (size * 30).dpf, paint)
            rectFShow.left = x - widthG
            rectFShow.right = x + widthG
            rectFShow.top = y + (size * 30).dpf
            rectFShow.bottom = y
            paint.color = Color.parseColor("#2B3444")
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(rectFShow, 10f, 10f, paint)
            /* text */
            val textH2 = textPaint.fontMetrics.let { font ->
                font.leading - font.ascent
            }
            var docY = y + 15.dpf
            val docX = x - widthG + 12.dpf
            textPaint.color = Color.WHITE
            lines.forEach {
                val data = it.list[iShowDayIncome]
                if (data.iGetValue() != -1.0) {
                    val text = data.iGetValueStr()
                    /* doc */
                    paint.color = it.color
                    canvas.drawCircle(docX, docY, 4.dpf, paint)
                    /* text */
                    textPaint.textAlign = Paint.Align.LEFT
                    textPaint.getTextBounds(text, 0, text.length, textRect)
                    canvas.drawText(text, docX + 8.dpf, docY + (textH2 / 2) - 1.dpf, textPaint)
                    docY += 30.dpf
                }
            }
        }
    }

    private val rectFShow = RectF()

    private val bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.icon_chart_down)
    }

    private var iDocStart = 0f
    private var iDocEnd = 0f
    private var iDocInterval = 0f
    private var iShowDayIncome = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    iGetRefresh(this)?.isEnabled = false
//                    parent.requestDisallowInterceptTouchEvent(true)
                }
                if (event.x in iDocStart..iDocEnd) {
                    val a = ((event.x - iDocStart + (iDocInterval / 2)) / iDocInterval).toInt()
                    if (a in 0..6) {
                        iShowDayIncome = a
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                iGetRefresh(this)?.isEnabled = true
//                parent.requestDisallowInterceptTouchEvent(false)
                iShowDayIncome = -1
                invalidate()
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
            iMaxValue > 1000 -> "${AMathUtils.formatSize(this / 1000.0, scale = 1)}k"
            else -> "$this"
        }
    }

    private val weeks = arrayOf("Mon", "Tue", "Wed", "Thur", "Thur", "Sat", "Sun")

    class LineCartData<T : LineNoteInterface>(val color: Int, val name: String, val list: List<T>)

    interface LineNoteInterface {

        fun iGetValue(): Double

        fun iGetValueStr(): String
    }
}
