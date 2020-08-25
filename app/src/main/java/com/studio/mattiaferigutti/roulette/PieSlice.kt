package com.studio.mattiaferigutti.roulette

import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import kotlin.math.*

/**
 * @author Mattia Ferigutti
 * There has to be no padding
 */
class PieSlice(private val numberOfPieSlices: Int, circleHeight: Float, circleWidth: Float, private val static: Boolean, middleCircle: Float) {
    /**
     *  360-12 = 348 -- 12 is the angle of the lock slide
     */
    val theta by lazy {
        if (static)
            360f/numberOfPieSlices
        else
            (360f - WheelView.startAngle)/numberOfPieSlices
    }
    private val topMiddleRadians = 270f.toRadians()
    private val thetaRadians: Float = theta.toRadians()
    private val halfThetaRadians = thetaRadians/2
    val centerCircle = min(circleHeight, circleWidth) /2
    private val radius = min(circleHeight, circleWidth) /2

    private var XCartesianFirst = radius * cos(topMiddleRadians-halfThetaRadians)
    private var YCartesianFirst = radius * sin(topMiddleRadians-halfThetaRadians)
    private var XCartesianSecond = radius * cos(topMiddleRadians+halfThetaRadians)
    private var YCartesianSecond = radius * sin(topMiddleRadians+halfThetaRadians)

    private var XCartesianFirstS = (radius*(1/1.5f)) * cos(topMiddleRadians-halfThetaRadians)
    private var YCartesianFirstS = (radius*(1/1.5f)) * sin(topMiddleRadians-halfThetaRadians)
    private var XCartesianSecondS = (radius*(1/1.5f)) * cos(topMiddleRadians+halfThetaRadians)
    private var YCartesianSecondS = (radius*(1/1.5f)) * sin(topMiddleRadians+halfThetaRadians)
    private var textSpaceHeight = (centerCircle + YCartesianFirstS) - middleCircle

    private val bigPieRect = RectF(0f, centerCircle + (radius* sin(topMiddleRadians)), min(circleHeight, circleWidth), min(circleHeight, circleWidth))
    private val smallPieRect = RectF(min(circleHeight, circleWidth) *((1*0.75f)/4f)-10f, centerCircle + ((radius*(1/1.5f)) * sin(topMiddleRadians)), centerCircle*2 - min(circleHeight, circleWidth) *(((1*0.75f)/4f))+10f, (centerCircle*2) - min(circleHeight, circleWidth) *(((1*0.75f)/4f))+8.5f)

    /**
     * @return the path of the big pie slice
     */
    fun getPathBigPie() : Path {
        return Path().apply {
            moveTo(centerCircle, centerCircle)
            lineTo(centerCircle + XCartesianFirst, centerCircle + YCartesianFirst) //left
            moveTo(centerCircle, centerCircle)
            lineTo(centerCircle + XCartesianSecond, centerCircle + YCartesianSecond) //right
            lineTo(centerCircle + XCartesianFirst, centerCircle + YCartesianFirst)
            addArc(bigPieRect, 270-(theta/2), theta) //in degrees
            //addRect(rect, Path.Direction.CW)
        }
    }

    /**
     * @return [RectF] which will be used to put pill icon inside
     * the measure of the rect (namely of the pill) is gotten in runtime 'cause is depend on the dimension of the view
     */
    private val width = ((centerCircle + XCartesianSecondS) - (centerCircle + XCartesianFirstS))*(1/1.5f)
    private val padding = width * 1/10f
    private val left = centerCircle + XCartesianFirstS + padding*2
    private val right = centerCircle + XCartesianSecondS - padding*2
    private val top = centerCircle + YCartesianFirstS - width + padding
    private val bottom = centerCircle + YCartesianFirstS - padding

    fun getPillIconRectFirst() : RectF {
        return RectF(left, top, right, bottom)
    }

    fun getPillIconRectSecond() : RectF {
        return RectF(left, top - width, right, bottom - width)
    }

    fun getPillIconRectThird() : RectF {
        return RectF(left, top - 2*width, right, bottom - 2*width)
    }

    /**
     * @return [RectF] which will be used to put the icon inside
     * The dimension of the rect if 20x20
     */
    fun getLockIconRect() : RectF {
        val imageSize = 20f
        val padding = 1.5f
        val left = centerCircle - imageSize/2
        val right = centerCircle + imageSize/2
        return RectF(left + padding, centerCircle + YCartesianFirst + imageSize/4, right - padding, centerCircle + YCartesianFirst + imageSize + imageSize/4)
    }


    /**
     * @return the path of the small pie slice
     */
    fun getPathSmallPie() : Path {
        return Path().apply {
            moveTo(centerCircle, centerCircle)
            lineTo(centerCircle + XCartesianFirstS, centerCircle + YCartesianFirstS) //left
            moveTo(centerCircle, centerCircle)
            lineTo(centerCircle + XCartesianSecondS, centerCircle + YCartesianSecondS) //right
            lineTo(centerCircle + XCartesianFirstS, centerCircle + YCartesianFirstS)
            addArc(smallPieRect, 270-(theta/2), theta) //in degrees
            //addRect(smallPieRect, Path.Direction.CW)
        }
    }

    /**
     * @return the path to draw the line above the pie slices to cover a grey line
     */
    fun getLineSmall() : Path {
        return Path().apply {
            moveTo(centerCircle + XCartesianSecondS, centerCircle + YCartesianSecondS)
            lineTo(centerCircle + XCartesianFirstS, centerCircle + YCartesianFirstS)
        }
    }

    /**
     * @return [PointF] of where to draw the text
     */
    fun getTextRect(textSize: Float, textPaint: TextPaint, string: String) : PointF {
        val height = centerCircle + YCartesianFirstS
        val rect = RectF(centerCircle + XCartesianFirstS, centerCircle + ((radius*(1/1.5f)) * sin(topMiddleRadians)), centerCircle + XCartesianSecondS, height - textSpaceHeight)
        val rectHeight = Rect()
        val cx = rect.centerX()
        val cy = rect.centerY()

        textPaint.getTextBounds(string, 0, string.length, rectHeight)
        val y = cy + rectHeight.height()/2
        val x = cx - textPaint.measureText(string)/2

        return PointF(x, y)
    }

    /**
     *  Used only for debug
     */
    fun getRectText(textSize: Float) : RectF {
        val height = centerCircle + YCartesianFirstS
        val padding = abs(textSpaceHeight * (1/4f))
        val rect = RectF(centerCircle + XCartesianFirstS, height + padding , centerCircle + XCartesianSecondS, height + textSize*1.5f + padding)
        val widthR = rect.width()
        val heightR = rect.height()
        return rect
    }

    /**
     * This method draws line between one pie slice and another
     */
    fun getPathLines() : Path {
        return Path().apply {
            moveTo(centerCircle, centerCircle)
            lineTo(centerCircle + XCartesianFirst, centerCircle + YCartesianFirst) //left
            moveTo(centerCircle, centerCircle)
        }
    }

    private fun Float.toRadians(): Float {
        return (this * PI / 180).toFloat()
    }
}