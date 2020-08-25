package com.studio.mattiaferigutti.roulette

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.*

class WheelView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    defStyleRes: Int) : View(context, attrs, defStyle, defStyleRes) {

    constructor(context: Context)
            : this(context, null, 0, 0)

    constructor(context: Context, attrs: AttributeSet)
            : this(context, attrs, 0, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : this(context, attrs, defStyle, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > height) height else width
        setMeasuredDimension(size, size)
    }

    /**
     * Variables and attrs definitions
     */
    private var pathLines: Path? = null
    private var pathBigPie: Path? = null
    private var pathSmallPie: Path? = null
    private var pathLineCover: Path? = null

    var numberOfSlices: Int = 25
        set(value) {
            field = value
            invalidate()
        }
    private var radius: Float = 0f
    private var theta: Float = 0f
    var emptyBinsList: MutableList<Int> = arrayListOf()
        set(value) {
            field = value
            invalidate()
        }
    var shadowSlicesList: MutableList<Int> = arrayListOf()
        set(value) {
            field = value
            invalidate()
        }
    private var pieSlice: PieSlice? = null
    private var pieSliceStatic : PieSlice? = null
    private var circleRadius = 0f
    private var pillsTotal = 0

    var shadowColorCircle: Int = resources.getColor(R.color.shadowColor)
        set(value) {
            field = value
            invalidate()
        }
    var shadowRadiusCircle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var shadowColorSlice: Int = resources.getColor(R.color.shadowColor)
        set(value) {
            field = value
            invalidate()
        }
    var shadowRadiusSlice: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var lockPieColor: Int = resources.getColor(R.color.lockPieColor)
        set(value) {
            field = value
            invalidate()
        }
    var lineStrokeSize: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    private val emptyBinColor: Int = resources.getColor(R.color.emptyBinColor)
    var lineColorBetweenSlices: Int = resources.getColor(R.color.emptyBinsLine)
        set(value) {
            field = value
            invalidate()
        }
    private var colorCircle: Int = resources.getColor(R.color.colorCircle)
    private var colorBigCircle: Int = resources.getColor(R.color.colorBigCircle)
    private var colorBigPie: Int = resources.getColor(R.color.colorBigPie)
    var colorOddSmallPie: Int = resources.getColor(R.color.colorOddSmallPie)
        set(value) {
            field = value
            invalidate()
        }
    var colorEvenSmallPie: Int = resources.getColor(R.color.colorEvenSmallPie)
        set(value) {
            field = value
            invalidate()
        }

    private val lockBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_lock_icon)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PillWheelView).apply {
            //numberOfSlices = getInteger(R.styleable.PillWheelView_stv_number_of_slices, numberOfSlices)
            lockPieColor = getColor(R.styleable.PillWheelView_stv_lock_pie_color, lockPieColor)
            colorOddSmallPie = getColor(R.styleable.PillWheelView_stv_color_odd_small_pie, colorOddSmallPie)
            colorEvenSmallPie = getColor(R.styleable.PillWheelView_stv_color_even_small_pie, colorEvenSmallPie)
            shadowRadiusCircle = getFloat(R.styleable.PillWheelView_stv_shadow_radius, shadowRadiusCircle)
            shadowColorCircle = getColor(R.styleable.PillWheelView_stv_color_shadow, shadowColorCircle)
            lineColorBetweenSlices = getColor(R.styleable.PillWheelView_stv_line_color_between_slices, lineColorBetweenSlices)
            lineStrokeSize = getFloat(R.styleable.PillWheelView_stv_line_stroke_size, lineStrokeSize)
            shadowRadiusSlice = getFloat(R.styleable.PillWheelView_stv_shadow_radius_slice, shadowRadiusSlice)
            shadowColorSlice = getColor(R.styleable.PillWheelView_stv_color_shadow_slices, shadowColorSlice)
            recycle()
        }
    }

    /**
     * Paint definitions
     */
    private val emptyBinSlice = Paint().apply {
        color = emptyBinColor
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val lockSlice = Paint().apply {
        color = lockPieColor
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val bigPiePaintWithShadow = Paint().apply {
        color = colorBigPie
        style = Paint.Style.FILL
        setShadowLayer(shadowRadiusSlice, 0f, 2f, shadowColorSlice)
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val smallOddPiePaint = Paint().apply {
        color = colorOddSmallPie
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val smallEvenPiePaint = Paint().apply {
        color = colorEvenSmallPie
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val smallEvenLineCoverPiePaint = Paint().apply {
        color = colorEvenSmallPie
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val smallOddLineCoverPiePaint = Paint().apply {
        color = colorOddSmallPie
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val circlePaint = Paint().apply {
        color = colorCircle
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
        setShadowLayer(shadowRadiusCircle, 0f, 2f, shadowColorCircle)
    }

    private val bigCirclePaint = Paint().apply {
        color = colorBigCircle
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val borderLinePaint = Paint().apply {
        color = lineColorBetweenSlices
        style = Paint.Style.STROKE
        strokeWidth = lineStrokeSize
        isAntiAlias = true
    }

    private val textPaint = TextPaint().apply {
        textSize = 12f
        color = colorCircle
        isAntiAlias = true
    }

    private val iconPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    override fun onDraw(canvas: Canvas) {
        update()
        canvas.drawCircle(width/2f, height/2f, width/2f, bigCirclePaint)
        drawSlices(canvas)
        drawShadowsForSlices(canvas)
        canvas.drawPath(pieSliceStatic?.getPathBigPie()!!, lockSlice)
        canvas.drawBitmap(lockBitmap, null, pieSliceStatic!!.getLockIconRect(), iconPaint)
        canvas.drawCircle(width/2f, height/2f, circleRadius, circlePaint)
        super.onDraw(canvas)
    }

    private fun drawText(canvas: Canvas, paint: TextPaint, text: String, pieSlice: PieSlice?, string: String) {
        val pointF = pieSlice?.getTextRect(paint.textSize, textPaint, string)
        canvas.drawText(text, pointF!!.x, pointF.y, paint)
    }

    /**
     * it sets the initial parameters
     */
    private fun update() {
        if (paddingBottom > 0 || paddingEnd > 0 || paddingLeft > 0 || paddingRight > 0 || paddingStart > 0 || paddingTop > 0)
            Throwable("this view cannot handle the padding, remove it and try again")
        circleRadius = width/4f
        pieSlice = PieSlice(numberOfSlices, width.toFloat(), height.toFloat(), false, circleRadius)
        pieSliceStatic = PieSlice(middleLockSlice, width.toFloat(), height.toFloat(), true, circleRadius)
        theta = abs((startAngle + ((pieSlice!!.theta/2))-(startAngle /2)))

        radius = width.toFloat()/2
        pathBigPie = pieSlice?.getPathBigPie()
        pathSmallPie = pieSlice?.getPathSmallPie()
        pathLines = pieSlice?.getPathLines()
        pathLineCover = pieSlice?.getLineSmall()
    }

    /**
     * It's used to draw slices
     */
    private var pieIndex = 0
    private fun drawSlices(canvas: Canvas) {
        for (i in 0 until numberOfSlices) {
            if(pieIndex < i) {
                emptyBinsList.add(pieIndex)
                pieIndex++
            }
            pieIndex++
        }
        for(i in 0 until numberOfSlices) {
            canvas.save()
            canvas.rotate(theta, pieSlice!!.centerCircle, pieSlice!!.centerCircle)
            drawPieSlice(canvas, i)
            drawLines(canvas)
            canvas.restore()
            theta += (360f - startAngle)/numberOfSlices
        }
    }

    /**
     * It's used to draw shadows on the slices if it is needed
     */
    private fun drawShadowsForSlices(canvas: Canvas) {
        if (shadowSlicesList.size > 0) {
            theta = abs((startAngle + ((pieSlice!!.theta/2))-(startAngle /2)))
            for(i in 0 until numberOfSlices) {
                canvas.save()
                canvas.rotate(theta, pieSlice!!.centerCircle, pieSlice!!.centerCircle)
                drawPieSliceWithShadows(canvas, i)
                canvas.restore()
                theta += (360f - startAngle)/numberOfSlices
            }
        }
    }

    /**
     * This method is called to draw pie slices
     */
    private fun drawPieSlice(canvas: Canvas, i: Int) {
        if (emptyBinsList.contains(i)) {
            canvas.drawPath(pathBigPie!!, emptyBinSlice)
            canvas.drawPath(pathSmallPie!!, emptyBinSlice)
            return
        }

        if (i%2 == 0) {
            //even
            if (numberOfSlices <= 7)
                canvas.drawPath(pathLineCover!!, smallEvenLineCoverPiePaint)
            canvas.drawPath(pathSmallPie!!, smallEvenPiePaint)
        } else {
            //odd
            if (numberOfSlices <= 7)
                canvas.drawPath(pathLineCover!!, smallOddLineCoverPiePaint)
            canvas.drawPath(pathSmallPie!!, smallOddPiePaint)
        }
        if (i<9)
            drawText(canvas, textPaint, "${i+1}", pieSlice, "${i+1}")
        else
            drawText(canvas, textPaint, "${i+1}", pieSlice, "${i+1}")
    }

    /**
     * This method is called after [drawPieSlice] to draw shadows above the others pie slices
     */
    private fun drawPieSliceWithShadows(canvas: Canvas, i: Int) {
        if (shadowSlicesList.contains(i))
            canvas.drawPath(pathBigPie!!, bigPiePaintWithShadow)
        else
            return

        if (i%2 == 0) {
            //even
            canvas.drawPath(pathSmallPie!!, smallEvenPiePaint)
        } else {
            //odd
            canvas.drawPath(pathSmallPie!!, smallOddPiePaint)
        }
        if (i<9)
            drawText(canvas, textPaint, "${i+1}", pieSlice, "${i+1}")
        else
            drawText(canvas, textPaint, "${i+1}", pieSlice, "${i+1}")
    }

    /**
     * This method draws line between one pie slice and another
     */
    private fun drawLines(canvas: Canvas) {
        canvas.drawPath(pathLines!!, borderLinePaint)
    }

    companion object {
        const val startAngle = 12f //dimension of the pink slice
        const val middleLockSlice = 30
    }

}