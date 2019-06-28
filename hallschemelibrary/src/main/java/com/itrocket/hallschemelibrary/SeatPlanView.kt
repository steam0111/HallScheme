package com.itrocket.hallschemelibrary

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.seat.Status
import com.itrocket.hallschemelibrary.seat.getRevertedStatus
import com.itrocket.hallschemelibrary.seat.isClickable
import kotlin.math.max

class SeatPlanView(context: Context, attrs: AttributeSet?) : ZoomableImageView(context, attrs) {

    private var seats : List<BaseSeat> = mutableListOf()
    private var legend : List<Legend>? = null
    private var maxCountSeatsInHeight = 0
    private var maxCountSeatsInWidth = 0
    private var isDrawScreen = true
    private var legendHeight = 0
    private var offsetForCenteredSeatPlan = 0
    private var bitmapWidth : Int = 0


    /**
     *  @param BaseSeat - clickable item that was clicked
     *  @param List<BaseSeat> - already clicked seats
     *  @return Boolean - choose redraw bitmap or not
     */
    private var clickedRuleForClickableItems : (BaseSeat, List<BaseSeat>) -> Boolean = {seat, seats ->
        false
    }

    fun drawSeatPlan(
        seats : List<BaseSeat>,
        enableZoom : Boolean,
        legend : List<Legend>? = null,
        isDrawScreen : Boolean,
        clickedRuleForClickableItems : (BaseSeat, List<BaseSeat>) -> Boolean = {seat, seats ->
            false
        }
    ) {
        if (seats.isNotEmpty()) {
            this.clickedRuleForClickableItems = clickedRuleForClickableItems
            this.isDrawScreen = isDrawScreen
            super.setZoomByDoubleTap(enableZoom)
            this.seats = seats
            this.legend = legend
            maxCountSeatsInHeight = seats.maxBy { it.row }?.row ?: 0
            maxCountSeatsInWidth = seats.maxBy { it.column }?.column ?: 0

            this.setImageBitmap(getSeatViewBitmap())

            super.setClickListener(object : ImageClickListener {
                override fun onClick(p: Point) {
                    if (p.y in 0..legendHeight) return

                    if (offsetForCenteredSeatPlan != 0 &&
                        (p.x in 0..offsetForCenteredSeatPlan ||
                         p.x in (bitmapWidth - offsetForCenteredSeatPlan)..bitmapWidth)) return

                    val correctPoint = Point((p.x - offsetForCenteredSeatPlan), (p.y - legendHeight))
                    clickScheme(correctPoint)
                }
            })
        }
    }

    private fun getSeatViewBitmap() : Bitmap {

        textPaint.textSize = TEXT_SIZE_LEGEND

        //calculate legend width, height
        val legendWidth : Int = legend?.sumBy {
            textPaint.getTextBounds(it.text, 0, it.text.length, textBounds)
            SEAT_WIDTH + textBounds.width() + SEAT_GAP*2
        } ?: 0

        legendHeight = if (legend != null) 2*SEAT_WIDTH else 0

        //+ SEAT_WIDTH bcs seat matrix start from 0
        val heightSeatGapOffset = ((maxCountSeatsInHeight) * SEAT_GAP)
        val widthSeatGapOffset = ((maxCountSeatsInWidth) * SEAT_GAP)

        //only seat matrix width
        val matrixWidth = maxCountSeatsInWidth * SEAT_WIDTH + SEAT_WIDTH + widthSeatGapOffset

        val bitmapHeight = maxCountSeatsInHeight * SEAT_WIDTH + SEAT_WIDTH + heightSeatGapOffset + legendHeight

        //choose btw matrixWidth and legendWidth
        bitmapWidth = max(matrixWidth, legendWidth)

        val tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)

        val offsetForCenteredLegend = if (legendWidth < matrixWidth) {
            (matrixWidth - legendWidth)/2
        } else 0

        legend?.let {
            drawLegend(
                tempCanvas,
                it,
                SEAT_WIDTH,
                Color.WHITE,
                TEXT_SIZE_LEGEND,
                offsetForCenteredLegend,
                SEAT_GAP.toFloat())
        }

        offsetForCenteredSeatPlan = if (matrixWidth < legendWidth) {
            (legendWidth - matrixWidth)/2
        } else 0

        seats.forEach {
            var leftTopX = if (it.column == 0) {
                it.column * SEAT_WIDTH
            } else {
                it.column * (SEAT_WIDTH + SEAT_GAP)
            }

            var leftTopY  = if (it.row == 0) {
                it.row * SEAT_WIDTH
            } else {
                it.row * (SEAT_WIDTH + SEAT_GAP)
            }

            var rightBottomX = if (it.column == 0) {
                it.column * SEAT_WIDTH + SEAT_WIDTH
            } else {
                it.column * (SEAT_WIDTH + SEAT_GAP) + SEAT_WIDTH
            }
            var rightBottomY = if (it.row == 0) {
                it.row * SEAT_WIDTH + SEAT_WIDTH
            } else {
                it.row * (SEAT_WIDTH + SEAT_GAP) + SEAT_WIDTH
            }

            leftTopX += offsetForCenteredSeatPlan
            rightBottomX += offsetForCenteredSeatPlan
            leftTopY += legendHeight
            rightBottomY += legendHeight

            val pointLeftTop = Point(leftTopX,leftTopY)
            val pointRightBottom = Point(rightBottomX,rightBottomY)

            it.drawSeat(
                tempCanvas,
                pointLeftTop,
                pointRightBottom,
                it.status
            )
        }

        return tempBitmap
    }

    /**
     *
     * @param canvas Canvas
     * @param legend List<Legend> - legend about seat plan
     * @param iconWidth Int
     * @param colorText Int
     * @param legendTextSize Float
     * @param offsetFromLeftSide - if seat plan width more than legend width, need to right move legend
     * @param offsetLineFromLegend
     */
    private fun drawLegend(canvas: Canvas,
                           legend: List<Legend>,
                           iconWidth : Int,
                           @ColorInt colorText : Int,
                           legendTextSize : Float,
                           offsetFromLeftSide : Int,
                           offsetLineFromLegend : Float) {

        var marginFromLeft = 0f
        var legendWidth = 0f
        legend.forEach { item ->

                var leftTopX = marginFromLeft.toInt()
                var rightBottomX = marginFromLeft.toInt() + iconWidth

                val leftTopY  = 0
                val rightBottomY = iconWidth

                leftTopX += offsetFromLeftSide
                rightBottomX += offsetFromLeftSide

                val pointLeftTop = Point(leftTopX,leftTopY)
                val pointRightBottom = Point(rightBottomX,rightBottomY)

                drawDrawable(
                    canvas,
                    item.drawableIcon,
                    pointLeftTop,
                    pointRightBottom)

                val textWidth = (item.text.length * legendTextSize)

                marginFromLeft += (iconWidth)

                val cx = marginFromLeft + (textWidth/2) + offsetFromLeftSide

                drawTextCentred(
                    item.text,
                    canvas,
                    colorText,
                    cx,
                    rightBottomY/2f,
                    textSize = legendTextSize)

                marginFromLeft += (textWidth/2 + SEAT_GAP*2)
                legendWidth = cx
        }

        canvas.drawLine(
            offsetFromLeftSide.toFloat(),
            iconWidth + offsetLineFromLegend,
            legendWidth,
            iconWidth + offsetLineFromLegend,
            linePaint)
    }

    private fun isClickSeat(row : Int, column : Int, seats: List<BaseSeat>) : BaseSeat?  {
        return seats
            .filter { it.status.isClickable() }
            .find { it.row == row && it.column == column }
    }

    private fun getClickedSeats(seats: List<BaseSeat>) : List<BaseSeat> {
        return seats
            .filter { it.status == Status.CLICKED }
    }

    private fun clickScheme(point: Point) {
        val column = point.x / (SEAT_WIDTH + SEAT_GAP)
        val row = point.y / (SEAT_WIDTH + SEAT_GAP)

        val clickedSeat = isClickSeat(row, column, seats)

        if (clickedSeat != null &&
            clickedRuleForClickableItems(
                clickedSeat,
                getClickedSeats(seats))) {
            this.setImageBitmap(getSeatViewBitmap())
        }
    }

    companion object {

        private const val TEXT_BORDER_WIDTH = 0.5f
        private const val SEAT_GAP = 6
        private const val SEAT_WIDTH = 30
        private const val TEXT_SIZE : Float = (SEAT_WIDTH/2f)
        private const val TEXT_SIZE_LEGEND : Float = (SEAT_WIDTH/3f)


        private val textBounds : Rect by lazy { Rect() }
        private val textPaint : Paint by lazy {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            textPaint.style = Paint.Style.FILL_AND_STROKE
            textPaint.textSize = TEXT_SIZE
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.strokeWidth = TEXT_BORDER_WIDTH
            return@lazy textPaint
        }

        private val linePaint : Paint by lazy {
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            linePaint.style = Paint.Style.FILL_AND_STROKE
            linePaint.color = Color.WHITE
            return@lazy linePaint
        }

        fun drawDrawable(canvas: Canvas,
                         drawable : Drawable,
                         pointLeftTop : Point,
                         pointRightBottom : Point) {
            drawable.setBounds(pointLeftTop.x, pointLeftTop.y, pointRightBottom.x, pointRightBottom.y)
            drawable.draw(canvas)
        }

        fun drawTextCentred(
            text: String,
            canvas: Canvas,
            @ColorInt colorInt: Int,
            cx: Float,
            cy: Float,
            textSize : Float = TEXT_SIZE) {
            textPaint.textSize = textSize
            textPaint.color = colorInt
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), textPaint)
        }
    }

}