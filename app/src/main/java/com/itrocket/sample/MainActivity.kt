package com.itrocket.sample

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.itrocket.hallschemelibrary.BaseSeat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val seatList = mutableListOf<BaseSeat>()

        val paint = Paint()
        paint.color = Color.RED

        val drawable = this.resources.getDrawable(R.drawable.ic_android_black_24dp, null)

        for (i in 0..10) {
            for (j in 0..10) {
                if (j % 2 == 0) {
                    val seat = DrawableSeat(drawable = drawable)

                    seat.column = j
                    seat.row = i

                    seatList.add(seat)

                } else  {
                    val textSeat = TextSeat(
                        "A",
                        paint)

                    textSeat.column = j
                    textSeat.row = i

                    seatList.add(textSeat)
                }
            }
        }

        seatPlanView.drawSeatPlan(
            seatList,
            true
        )
    }
}
