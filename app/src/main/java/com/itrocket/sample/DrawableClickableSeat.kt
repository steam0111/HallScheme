package com.itrocket.sample

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import com.itrocket.hallschemelibrary.SeatPlanView
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.seat.SeatStatus

class DrawableClickableSeat(var drawable : Drawable,
                            var drawableOn : Drawable) : BaseSeat() {

    override fun drawSeat(canvas: Canvas, pointLeftTop: Point, pointRightBottom: Point, seatStatus: SeatStatus) {

        val drawDrawable = if (seatStatus == SeatStatus.CLICKED) {
            drawableOn
        } else {
            drawable
        }

        SeatPlanView.drawDrawable(canvas, drawDrawable, pointLeftTop, pointRightBottom)
    }
}