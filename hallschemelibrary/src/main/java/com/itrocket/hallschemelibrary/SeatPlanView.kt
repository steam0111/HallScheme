package com.itrocket.hallschemelibrary

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.itrocket.hallschemelibrary.legend.Legend
import com.itrocket.hallschemelibrary.screen.Screen
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.seat.SeatStatus
import com.itrocket.hallschemelibrary.seat.isClickable
import kotlin.math.max


class SeatPlanView(context: Context, attrs: AttributeSet?) : ZoomableImageView(context, attrs) {

    private var seats: List<BaseSeat> = mutableListOf()
    private var legend: List<Legend>? = null
    private var maxCountSeatsInHeight = 0
    private var maxCountSeatsInWidth = 0
    private var legendHeight = 0
    private var offsetForCenteredSeatPlan = 0
    private var bitmapWidth: Int = 0
    private var seatGap = 6
    private var screenHeightFactorInSeatSize = 2 //screen height in chair height
    private var isHaveScreen = false
    private var screen: Screen? = null
    private val screenPaint: Paint by lazy {
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.style = Paint.Style.FILL
        linePaint.color = Color.RED
        return@lazy linePaint
    }

    /**
     *  @param BaseSeat - clickable item that was clicked
     *  @param List<BaseSeat> - already clicked seats
     *  @return Boolean - choose redraw bitmap or not
     */
    private var clickedRuleForClickableItems: (BaseSeat, List<BaseSeat>) -> Boolean =
        { seat, seats ->
            false
        }

    fun getClickedSeats(): List<BaseSeat> = seats.filter { it.seatStatus == SeatStatus.CLICKED }

    fun drawSeatPlan(
        seats: List<BaseSeat>,
        enableZoom: Boolean,
        legend: List<Legend>? = null,
        clickedRuleForClickableItems: (BaseSeat, List<BaseSeat>) -> Boolean = { seat, seats ->
            false
        },
        seatGap: Int = this.seatGap,
        screen: Screen? = null
    ) {

        this.seatGap = seatGap
        this.screen = screen

        if (seats.isNotEmpty()) {
            super.setZoomable(enableZoom)

            this.clickedRuleForClickableItems = clickedRuleForClickableItems
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
                                p.x in (bitmapWidth - offsetForCenteredSeatPlan)..bitmapWidth)
                    ) return

                    val correctPoint =
                        Point((p.x - offsetForCenteredSeatPlan), (p.y - legendHeight))
                    clickScheme(correctPoint)
                }
            })
        }
    }

    private fun getSeatViewBitmap(): Bitmap {

        textPaint.textSize = TEXT_SIZE_LEGEND

        //calculate legend width, height
        val legendWidth: Int = legend?.sumBy {
            textPaint.getTextBounds(it.text, 0, it.text.length, textBounds)
            SEAT_WIDTH + textBounds.width() + seatGap * 2
        } ?: 0

        legendHeight = if (legend != null) 2 * SEAT_WIDTH else 0

        //+ SEAT_WIDTH bcs seat matrix start from 0
        val heightSeatGapOffset = ((maxCountSeatsInHeight) * seatGap)
        val widthSeatGapOffset = ((maxCountSeatsInWidth) * seatGap)

        //only seat matrix width
        val matrixWidth = maxCountSeatsInWidth * SEAT_WIDTH + SEAT_WIDTH + widthSeatGapOffset

        //if we have screen we calculate additional height with space
        val screenHeightWithSpace = if (screen != null) {
            (SEAT_WIDTH * screenHeightFactorInSeatSize) + SEAT_WIDTH
        } else {
            0
        }

        val bitmapHeight =
            maxCountSeatsInHeight * SEAT_WIDTH + SEAT_WIDTH +
                    heightSeatGapOffset +
                    legendHeight +
                    screenHeightWithSpace

        //choose btw matrixWidth and legendWidth
        bitmapWidth = max(matrixWidth, legendWidth)

        val tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)

        val offsetForCenteredLegend = if (legendWidth < matrixWidth) {
            (matrixWidth - legendWidth) / 2
        } else 0

        legend?.let {
            drawLegend(
                tempCanvas,
                it,
                SEAT_WIDTH,
                Color.WHITE,
                TEXT_SIZE_LEGEND,
                offsetForCenteredLegend,
                seatGap.toFloat()
            )
        }

        offsetForCenteredSeatPlan = if (matrixWidth < legendWidth) {
            (legendWidth - matrixWidth) / 2
        } else 0

        seats.forEach {
            var leftTopX = if (it.column == 0) {
                it.column * SEAT_WIDTH
            } else {
                it.column * (SEAT_WIDTH + seatGap)
            }

            var leftTopY = if (it.row == 0) {
                it.row * SEAT_WIDTH
            } else {
                it.row * (SEAT_WIDTH + seatGap)
            }

            var rightBottomX = if (it.column == 0) {
                it.column * SEAT_WIDTH + SEAT_WIDTH
            } else {
                it.column * (SEAT_WIDTH + seatGap) + SEAT_WIDTH
            }
            var rightBottomY = if (it.row == 0) {
                it.row * SEAT_WIDTH + SEAT_WIDTH
            } else {
                it.row * (SEAT_WIDTH + seatGap) + SEAT_WIDTH
            }

            leftTopX += offsetForCenteredSeatPlan
            rightBottomX += offsetForCenteredSeatPlan
            leftTopY += legendHeight
            rightBottomY += legendHeight

            val pointLeftTop = Point(leftTopX, leftTopY)
            val pointRightBottom = Point(rightBottomX, rightBottomY)

            it.drawSeat(
                tempCanvas,
                pointLeftTop,
                pointRightBottom,
                it.seatStatus,
                this
            )
        }

        screen?.let {
            drawScreen(
                tempCanvas, it, Rect(
                    0, tempBitmap.height - screenHeightWithSpace + SEAT_WIDTH,
                    tempBitmap.width, tempBitmap.height
                ),
                (tempBitmap.width / 2).toFloat(),
                (tempBitmap.height - (screenHeightWithSpace + SEAT_WIDTH) / 4).toFloat()
            )
        }

        return tempBitmap
    }

    private fun drawScreen(
        canvas: Canvas,
        screen: Screen,
        rect: Rect,
        textCx: Float,
        textCy: Float
    ) {
        screenPaint.color = screen.backgroundColorInt
        canvas.drawRect(rect, screenPaint)
        textPaint.textSize = screen.textSize
        textPaint.color = screen.textColorInt
        val yPos = (textCy - (textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(screen.text, textCx, yPos, textPaint)
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
    private fun drawLegend(
        canvas: Canvas,
        legend: List<Legend>,
        iconWidth: Int,
        @ColorInt colorText: Int,
        legendTextSize: Float,
        offsetFromLeftSide: Int,
        offsetLineFromLegend: Float
    ) {

        var marginFromLeft = 0f
        var legendWidth = 0f
        legend.forEach { item ->

            var leftTopX = marginFromLeft.toInt()
            var rightBottomX = marginFromLeft.toInt() + iconWidth

            val leftTopY = 0

            leftTopX += offsetFromLeftSide
            rightBottomX += offsetFromLeftSide

            val pointLeftTop = Point(leftTopX, leftTopY)
            val pointRightBottom = Point(rightBottomX, iconWidth)

            drawDrawable(
                canvas,
                this.resources.getDrawable(item.drawableResId, null),
                pointLeftTop,
                pointRightBottom
            )

            val textWidth = (item.text.length * legendTextSize)

            marginFromLeft += (iconWidth)

            val cx = marginFromLeft + (textWidth / 2) + offsetFromLeftSide

            drawTextCentred(
                item.text,
                canvas,
                colorText,
                cx,
                iconWidth / 2f,
                textSize = legendTextSize
            )

            marginFromLeft += (textWidth / 2 + seatGap * 2)
            legendWidth = cx
        }

        canvas.drawLine(
            offsetFromLeftSide.toFloat(),
            iconWidth + offsetLineFromLegend,
            legendWidth,
            iconWidth + offsetLineFromLegend,
            linePaint
        )
    }

    private fun isClickSeat(row: Int, column: Int, seats: List<BaseSeat>): BaseSeat? {
        return seats
            .filter { it.seatStatus.isClickable() }
            .find { it.row == row && it.column == column }
    }

    private fun getClickedSeats(seats: List<BaseSeat>): List<BaseSeat> {
        return seats
            .filter { it.seatStatus == SeatStatus.CLICKED }
    }

    private fun clickScheme(point: Point) {
        val column = point.x / (SEAT_WIDTH + seatGap)
        val row = point.y / (SEAT_WIDTH + seatGap)

        val clickedSeat = isClickSeat(row, column, seats)

        if (clickedSeat != null &&
            clickedRuleForClickableItems(
                clickedSeat,
                getClickedSeats(seats)
            )
        ) {
            this.setImageBitmap(getSeatViewBitmap())
        }
    }

    companion object {

        private const val TEXT_BORDER_WIDTH = 0.5f
        private const val SEAT_WIDTH = 30
        private const val TEXT_SIZE: Float = (SEAT_WIDTH / 2f)
        private const val TEXT_SIZE_LEGEND: Float = (SEAT_WIDTH / 3f)


        private val textBounds: Rect by lazy { Rect() }
        private val textPaint: Paint by lazy {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            textPaint.style = Paint.Style.FILL_AND_STROKE
            textPaint.textSize = TEXT_SIZE
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.strokeWidth = TEXT_BORDER_WIDTH
            return@lazy textPaint
        }

        private val linePaint: Paint by lazy {
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            linePaint.style = Paint.Style.FILL_AND_STROKE
            linePaint.color = Color.WHITE
            return@lazy linePaint
        }

        fun drawDrawable(
            canvas: Canvas,
            drawable: Drawable,
            pointLeftTop: Point,
            pointRightBottom: Point
        ) {
            drawable.setBounds(
                pointLeftTop.x,
                pointLeftTop.y,
                pointRightBottom.x,
                pointRightBottom.y
            )
            drawable.draw(canvas)
        }

        fun drawTextCentred(
            text: String,
            canvas: Canvas,
            @ColorInt colorInt: Int,
            cx: Float,
            cy: Float,
            textSize: Float = TEXT_SIZE
        ) {
            textPaint.textSize = textSize
            textPaint.color = colorInt
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(
                text,
                cx - textBounds.exactCenterX(),
                cy - textBounds.exactCenterY(),
                textPaint
            )
        }
    }

}