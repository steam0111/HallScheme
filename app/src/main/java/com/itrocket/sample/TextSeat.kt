package com.itrocket.sample

import android.graphics.Canvas
import android.graphics.Point
import androidx.annotation.ColorInt
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.SeatPlanView
import com.itrocket.hallschemelibrary.seat.Status

class TextSeat(var text : String, @ColorInt val color : Int) : BaseSeat() {
    override fun drawSeat(canvas: Canvas, pointLeftTop: Point, pointRightBottom: Point, status: Status) {
        val cx = pointLeftTop.x + (pointRightBottom.x - pointLeftTop.x)/2
        val cy =  pointLeftTop.y + (pointRightBottom.y - pointLeftTop.y)/2
        SeatPlanView.drawTextCentred(text, canvas, color, cx.toFloat(), cy.toFloat())
    }
}