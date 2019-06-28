package com.itrocket.sample

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.itrocket.hallschemelibrary.BaseSeat
import com.itrocket.hallschemelibrary.Legend
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val seatList = mutableListOf<BaseSeat>()

        val paint = Paint()
        paint.color = Color.RED

        val seatDrawable = this.resources.getDrawable(R.drawable.ic_android_black_24dp, null)
        val legendDrawable = this.resources.getDrawable(R.drawable.abc_ic_ab_back_material, null)

        for (i in 0..10) {
            for (j in 0..20) {
                if (j % 2 == 0) {
                    val seat = DrawableSeat(drawable = seatDrawable)

                    seat.column = j
                    seat.row = i

                    seatList.add(seat)

                } else  {
                    val textSeat = TextSeat(
                        "B",
                        Color.WHITE)

                    textSeat.column = j
                    textSeat.row = i

                    seatList.add(textSeat)
                }
            }
        }

        seatPlanView.drawSeatPlan(
            seatList,
            true,
            legend = listOf(
                Legend(legendDrawable, "legenda 1"),
                Legend(legendDrawable, "legenda 2"),
                Legend(legendDrawable, "legenda 2"),
                Legend(legendDrawable, "legenda 3")
            )
        )
    }
}
