package com.itrocket.sample.seats

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import com.itrocket.hallschemelibrary.SeatPlanView
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.seat.SeatStatus

//If you want to draw drawable
class DrawableClickableSeat(
    var drawableResId: Int, //pass drawable res off
    var drawableOnResId: Int
) //pass drawable res on
//inherit from BaseSeat
    : BaseSeat() {

    //draw your own seat as you want
    override fun drawSeat(
        canvas: Canvas,
        pointLeftTop: Point,
        pointRightBottom: Point,
        seatStatus: SeatStatus,
        seatPlanView: SeatPlanView
    ) {

        //get drawable from resources
        val drawDrawable = if (seatStatus == SeatStatus.CLICKED) {
            seatPlanView.context.getDrawable(drawableResId)
        } else {
            seatPlanView.context.getDrawable(drawableOnResId)
        }

        //can use some support function from SeatPlanView
        SeatPlanView
            .drawDrawable(
                canvas, drawDrawable, pointLeftTop, pointRightBottom
            )


        //if you wanna draw text over cell
        val textCy = pointRightBottom.y - ((pointRightBottom.y - pointLeftTop.y) / 2)
        val yPos =
            (textCy - (SeatPlanView.textPaint.descent() + SeatPlanView.textPaint.ascent()) / 2)

        SeatPlanView.textPaint.color = Color.WHITE

        canvas.drawText(
            "1",
            (pointRightBottom.x - ((pointRightBottom.x - pointLeftTop.x) / 2).toFloat()),
            yPos,
            SeatPlanView.textPaint
        )
    }
}