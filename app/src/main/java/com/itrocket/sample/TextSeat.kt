package com.itrocket.sample

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import com.itrocket.hallschemelibrary.BaseSeat
import com.itrocket.hallschemelibrary.SeatPlanView

class TextSeat(var text : String, val paint : Paint) : BaseSeat() {
    override fun drawSeat(canvas: Canvas, pointLeftTop: Point, pointRightBottom: Point) {
        val cx = pointLeftTop.x + (pointRightBottom.x - pointLeftTop.x)/2
        val cy =  pointLeftTop.y + (pointRightBottom.y - pointLeftTop.y)/2
        SeatPlanView.drawTextCentred(text, canvas, paint, cx.toFloat(), cy.toFloat())
    }
}