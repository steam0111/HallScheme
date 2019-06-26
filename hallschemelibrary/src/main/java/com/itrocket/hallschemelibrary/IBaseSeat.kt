package com.itrocket.hallschemelibrary

import android.graphics.Canvas
import android.graphics.Point

interface IBaseSeat {
    fun drawSeat(canvas: Canvas, pointLeftTop : Point, pointRightBottom : Point)
}