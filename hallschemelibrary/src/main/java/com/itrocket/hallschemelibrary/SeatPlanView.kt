package com.itrocket.hallschemelibrary

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt

class SeatPlanView(context: Context, attrs: AttributeSet?) : ZoomableImageView(context, attrs) {

    private val seatGap = 6

    private var seats : List<BaseSeat> = mutableListOf()
    private var maxCountSeatsInHeight = 0
    private var maxCountSeatsInWidth = 0

    fun drawSeatPlan(
        seats : List<BaseSeat>,
        enableZoom : Boolean
    ) {
        if (seats.isNotEmpty()) {
            super.setZoomByDoubleTap(enableZoom)
            this.seats = seats
            maxCountSeatsInHeight = seats.maxBy { it.row }?.row ?: 0
            maxCountSeatsInWidth = seats.maxBy { it.column }?.column ?: 0
            this.setImageBitmap(getBitmap())
        }

    }

    private fun getBitmap() : Bitmap {

        //+ SEAT_WIDTH bcs seat matrix start from 0
        val heightSeatGapOffset = ((maxCountSeatsInHeight) * seatGap)
        val widthSeatGapOffset = ((maxCountSeatsInWidth) * seatGap)

        val bitmapHeight = maxCountSeatsInHeight * SEAT_WIDTH + SEAT_WIDTH + heightSeatGapOffset
        val bitmapWidth = maxCountSeatsInWidth * SEAT_WIDTH + SEAT_WIDTH + widthSeatGapOffset

        val tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)

        seats.forEach {

            val leftTopX = if (it.column == 0) {
                it.column * SEAT_WIDTH
            } else {
                it.column * (SEAT_WIDTH + seatGap)
            }

            val leftTopY  = if (it.row == 0) {
                it.row * SEAT_WIDTH
            } else {
                it.row * (SEAT_WIDTH + seatGap)
            }

            val rightBottomX = if (it.column == 0) {
                it.column * SEAT_WIDTH + SEAT_WIDTH
            } else {
                it.column * (SEAT_WIDTH + seatGap) + SEAT_WIDTH
            }
            val rightBottomY = if (it.row == 0) {
                it.row * SEAT_WIDTH + SEAT_WIDTH
            } else {
                it.row * (SEAT_WIDTH + seatGap) + SEAT_WIDTH
            }

            val pointLeftTop = Point(leftTopX,leftTopY)
            val pointRightBottom = Point(rightBottomX,rightBottomY)

            it.drawSeat(
                tempCanvas,
                pointLeftTop,
                pointRightBottom)
        }

        return tempBitmap
    }

    companion object {

        private const val TEXT_BORDER_WIDTH = 1f
        private const val SEAT_WIDTH = 30
        

        private val textBounds : Rect by lazy { Rect() }
        private val textPaint : Paint by lazy {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            textPaint.style = Paint.Style.FILL_AND_STROKE
            textPaint.textSize = (SEAT_WIDTH/2).toFloat()
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.strokeWidth = TEXT_BORDER_WIDTH
            return@lazy textPaint
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
            cy: Float) {
            textPaint.color = colorInt
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), textPaint)
        }
    }

}