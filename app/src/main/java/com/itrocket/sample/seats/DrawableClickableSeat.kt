package com.itrocket.sample.seats

import android.graphics.Canvas
import android.graphics.Point
import com.itrocket.hallschemelibrary.SeatPlanView
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.seat.SeatStatus

class DrawableClickableSeat(var drawableResId : Int,
                            var drawableOnResId : Int) : BaseSeat() {

    override fun drawSeat(canvas: Canvas,
                          pointLeftTop: Point,
                          pointRightBottom: Point,
                          seatStatus: SeatStatus,
                          seatPlanView: SeatPlanView) {

        val drawDrawable = if (seatStatus == SeatStatus.CLICKED) {
            seatPlanView.context.getDrawable(drawableResId)
        } else {
            seatPlanView.context.getDrawable(drawableOnResId)
        }

        SeatPlanView.drawDrawable(canvas, drawDrawable, pointLeftTop, pointRightBottom)
    }
}