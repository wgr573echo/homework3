package com.bytedance.compicatedcomponent.homework

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 *  author : neo
 *  time   : 2021/10/25
 *  desc   :
 */
class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val FULL_ANGLE = 360

        private const val CUSTOM_ALPHA = 140
        private const val FULL_ALPHA = 255

        private const val POINTER_TYPE_SECOND = 2
        private const val POINTER_TYPE_MINUTES = 1
        private const val POINTER_TYPE_HOURS = 0

        private const val DEFAULT_PRIMARY_COLOR: Int = Color.WHITE
        private const val DEFAULT_SECONDARY_COLOR: Int = Color.LTGRAY

        private const val DEFAULT_DEGREE_STROKE_WIDTH = 0.010f

        private const val RIGHT_ANGLE = 90

        private const val UNIT_DEGREE = (6 * Math.PI / 180).toFloat() // 一个小格的度数

        private val textArray = arrayOf("12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
    }

    private var panelRadius = 200.0f // 表盘半径

    private var hourPointerLength = 0f // 指针长度

    private var minutePointerLength = 0f
    private var secondPointerLength = 0f

    private var resultWidth = 0
    private  var centerX: Int = 0
    private  var centerY: Int = 0
    private  var radius: Int = 0

    private var degreesColor = 0

    private val needlePaint: Paint



    init {
        degreesColor = DEFAULT_PRIMARY_COLOR
        needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        needlePaint.style = Paint.Style.FILL_AND_STROKE
        needlePaint.strokeCap = Paint.Cap.ROUND

//        val handler = Handler()
//        val runnable = Runnable { //1s重绘一次
//            invalidate()
//        }
//        handler.postDelayed(runnable, 1000)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size: Int
        val width = measuredWidth
        val height = measuredHeight
        val widthWithoutPadding = width - paddingLeft - paddingRight
        val heightWithoutPadding = height - paddingTop - paddingBottom
        size = if (widthWithoutPadding > heightWithoutPadding) {
            heightWithoutPadding
        } else {
            widthWithoutPadding
        }
        setMeasuredDimension(size + paddingLeft + paddingRight, size + paddingTop + paddingBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        resultWidth = if (height > width) width else height
        val halfWidth = resultWidth / 2
        centerX = halfWidth
        centerY = halfWidth
        radius = halfWidth
        panelRadius = radius.toFloat()
        hourPointerLength = panelRadius - 400
        minutePointerLength = panelRadius - 250
        secondPointerLength = panelRadius - 150
        drawDegrees(canvas)//表盘的刻度
        drawHoursValues(canvas)//画数字时间
        drawNeedles(canvas)//画指针

        // todo 1: 每一秒刷新一次，让指针动起来
        postInvalidateDelayed(1000);
    }

    private fun drawDegrees(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE//填充内部和描边
            strokeCap = Paint.Cap.ROUND//笔刷末端为圆形
            strokeWidth = resultWidth * DEFAULT_DEGREE_STROKE_WIDTH//线宽
            color = degreesColor
        }
        val rPadded: Int = centerX - (resultWidth * 0.01f).toInt()
        val rEnd: Int = centerX - (resultWidth * 0.05f).toInt()
        val textR = (measuredWidth / 2 - 50).toFloat()
        var i = 0
        while (i < FULL_ANGLE) {
            if (i % RIGHT_ANGLE != 0 && i % 15 != 0) {
                paint.alpha = CUSTOM_ALPHA
            } else {//等于90°整数倍、15°整数倍的时候
                paint.alpha = FULL_ALPHA//透明度变成不透明
            }
            val startX = (centerX + rPadded * cos(Math.toRadians(i.toDouble())))//Math.toRadians将角度转换为弧度
            val startY = (centerX - rPadded * sin(Math.toRadians(i.toDouble())))
            val stopX = (centerX + rEnd * cos(Math.toRadians(i.toDouble())))
            val stopY = (centerX - rEnd * sin(Math.toRadians(i.toDouble())))
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                stopX.toFloat(),
                stopY.toFloat(),
                paint
            )

            i += 6
        }
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private fun drawHoursValues(canvas: Canvas) {

        val paint = Paint().apply {
            textSize = 60f
            isAntiAlias = true
            color = Color.WHITE
        }
        val textR = (centerX - (resultWidth * 0.05f) - 50).toFloat()//文字构成的圆的半径
        for (i in 0..11) {
            //绘制文字的起始坐标
            val startX = (measuredWidth / 2 + textR * sin(Math.PI / 6 * i) - paint.measureText(textArray[i]) / 2).toFloat()
            val startY = (measuredHeight / 2 - textR * cos(Math.PI / 6 * i) + paint.measureText(textArray[i]) / 2).toFloat()
            canvas?.drawText(textArray[i], startX, startY,paint)
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private fun drawNeedles(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        val now: Date = calendar.time
        val nowHours: Int = now.hours
        val nowMinutes: Int = now.minutes
        val nowSeconds: Int = now.seconds
        // 画秒针
        drawPointer(canvas, POINTER_TYPE_SECOND, nowSeconds)
        // 画分针
        // todo 2: 画分针
        drawPointer(canvas, POINTER_TYPE_MINUTES,nowMinutes)
        // 画时针
        val part = nowMinutes / 12
        Log.i("timer","hour now is ${5*nowHours+part-8}")
        drawPointer(canvas, POINTER_TYPE_HOURS,  (5*nowHours+part-8)%12 )
    }


    private fun drawPointer(canvas: Canvas, pointerType: Int, value: Int) {
        val degree: Float
        var pointerHeadXY = FloatArray(2)
        needlePaint.strokeWidth = resultWidth * DEFAULT_DEGREE_STROKE_WIDTH
        when (pointerType) {
            POINTER_TYPE_HOURS -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.WHITE
                pointerHeadXY = getPointerHeadXY(hourPointerLength, degree)
            }
            POINTER_TYPE_MINUTES -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.YELLOW
                pointerHeadXY = getPointerHeadXY(minutePointerLength, degree)
            }
            POINTER_TYPE_SECOND -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.GREEN
                pointerHeadXY = getPointerHeadXY(secondPointerLength, degree)
            }
        }
        canvas.drawLine(
            centerX.toFloat(), centerY.toFloat(),
            pointerHeadXY[0], pointerHeadXY[1], needlePaint
        )
    }

    private fun getPointerHeadXY(pointerLength: Float, degree: Float): FloatArray {
        val xy = FloatArray(2)
        xy[0] = centerX + pointerLength * sin(degree)
        xy[1] = centerY - pointerLength * cos(degree)
        return xy
    }
}