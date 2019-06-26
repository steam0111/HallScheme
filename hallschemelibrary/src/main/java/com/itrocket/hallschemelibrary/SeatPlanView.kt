package com.itrocket.hallschemelibrary

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet

class SeatPlanView(context: Context, attrs: AttributeSet?) : ZoomableImageView(context, attrs) {

    var seatWidth = 30
    val seatGap = 6

    var seats : List<BaseSeat> = mutableListOf()

    var maxCountSeatsInHeight = 0
    var maxCountSeatsInWidth = 0

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

        //+ seatWidth bcs seat matrix start from 0
        val heightSeatGapOffset = ((maxCountSeatsInHeight) * seatGap)
        val widthSeatGapOffset = ((maxCountSeatsInWidth) * seatGap)

        val bitmapHeight = maxCountSeatsInHeight * seatWidth + seatWidth + heightSeatGapOffset
        val bitmapWidth = maxCountSeatsInWidth * seatWidth + seatWidth + widthSeatGapOffset

        val tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)

        seats.forEach {

            val leftTopX = if (it.column == 0) {
                it.column * seatWidth
            } else {
                it.column * (seatWidth + seatGap)
            }

            val leftTopY  = if (it.row == 0) {
                it.row * seatWidth
            } else {
                it.row * (seatWidth + seatGap)
            }

            val rightBottomX = if (it.column == 0) {
                it.column * seatWidth + seatWidth
            } else {
                it.column * (seatWidth + seatGap) + seatWidth
            }
            val rightBottomY = if (it.row == 0) {
                it.row * seatWidth + seatWidth
            } else {
                it.row * (seatWidth + seatGap) + seatWidth
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

        private val textBounds : Rect by lazy { Rect() }

        fun drawDrawable(canvas: Canvas,
                         drawable : Drawable,
                         pointLeftTop : Point,
                         pointRightBottom : Point) {
            drawable.setBounds(pointLeftTop.x, pointLeftTop.y, pointRightBottom.x, pointRightBottom.y)
            drawable.draw(canvas)
        }

        fun drawTextCentred(text: String, canvas: Canvas, paint: Paint, cx: Float, cy: Float) {
            paint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint)
        }
    }

}