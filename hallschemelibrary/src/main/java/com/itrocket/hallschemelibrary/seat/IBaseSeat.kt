package com.itrocket.hallschemelibrary.seat

import android.graphics.Canvas
import android.graphics.Point
import com.itrocket.hallschemelibrary.SeatPlanView

interface IBaseSeat {
    fun drawSeat(canvas: Canvas,
                 pointLeftTop : Point,
                 pointRightBottom : Point,
                 seatStatus: SeatStatus,
                 seatPlanView: SeatPlanView)
}