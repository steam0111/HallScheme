package com.itrocket.sample

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import com.itrocket.hallschemelibrary.BaseSeat
import com.itrocket.hallschemelibrary.SeatPlanView

class DrawableSeat(var drawable : Drawable) : BaseSeat() {

    override fun drawSeat(canvas: Canvas, pointLeftTop: Point, pointRightBottom: Point) {
        SeatPlanView.drawDrawable(canvas, drawable, pointLeftTop, pointRightBottom)
    }
}