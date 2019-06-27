package com.itrocket.sample

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import androidx.annotation.ColorInt
import com.itrocket.hallschemelibrary.BaseSeat
import com.itrocket.hallschemelibrary.SeatPlanView

class TextSeat(var text : String, @ColorInt val color : Int) : BaseSeat() {
    override fun drawSeat(canvas: Canvas, pointLeftTop: Point, pointRightBottom: Point) {
        val cx = pointLeftTop.x + (pointRightBottom.x - pointLeftTop.x)/2
        val cy =  pointLeftTop.y + (pointRightBottom.y - pointLeftTop.y)/2
        SeatPlanView.drawTextCentred(text, canvas, color, cx.toFloat(), cy.toFloat())
    }
}