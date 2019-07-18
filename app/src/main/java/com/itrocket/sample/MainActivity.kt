package com.itrocket.sample

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.itrocket.hallschemelibrary.Legend
import com.itrocket.hallschemelibrary.seat.BaseSeat
import com.itrocket.hallschemelibrary.seat.SeatStatus
import com.itrocket.hallschemelibrary.seat.getRevertedStatus
import com.itrocket.sample.seats.DrawableClickableSeat
import com.itrocket.sample.seats.TextSeat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seatList = mutableListOf<BaseSeat>()

        val paint = Paint()
        paint.color = Color.RED

        val legendDrawable = this.resources.getDrawable(R.drawable.abc_ic_ab_back_material, null)

        for (i in 0..4) {
            for (j in 0..15) {
                if (j % 4 == 0) {
                    val textSeat = TextSeat(
                        "B",
                        Color.WHITE
                    )

                    textSeat.column = j
                    textSeat.row = i
                    textSeat.seatStatus = SeatStatus.NOT_CLICKABLE

                    seatList.add(textSeat)

                } else  {

                    val seat = DrawableClickableSeat(
                        R.drawable.ic_android_red_24dp,
                        R.drawable.ic_android_black_24dp
                    )

                    seat.column = j
                    seat.row = i
                    seat.seatStatus = SeatStatus.NOT_CLICKED

                    seatList.add(seat)
                }
            }
        }

        btnGetClickedCount.setOnClickListener {
            Toast.makeText(this, seatPlanView.getClickedSeats().size.toString(), Toast.LENGTH_SHORT).show()
        }

        seatPlanView.drawSeatPlan(
            seatList,
            true,
            legend = listOf(
                Legend(legendDrawable, "legenda 1"),
                Legend(legendDrawable, "legenda 2"),
                Legend(legendDrawable, "legenda 2"),
                Legend(legendDrawable, "legenda 3")
            ),
            clickedRuleForClickableItems = { seat, seats ->

                when {
                    //if we click not near seat with left or right space
                    !seats.isSeatNearWithOthers(seat) -> {
                        seat.seatStatus = seat.seatStatus.getRevertedStatus()
                        seats.revertClickedStatuses()
                        true
                    }

                    //if we clicked not the same row like other
                    seats.isNotEmpty() && seat.row != seats.first().row -> {
                        seat.seatStatus = seat.seatStatus.getRevertedStatus()
                        seats.revertClickedStatuses()
                        true
                    }

                    //if clicked already clicked seat
                    seats.contains(seat) -> {
                        seat.seatStatus = seat.seatStatus.getRevertedStatus()
                        true
                    }

                    //can't click more than 8 seats
                    seats.size < 8 -> {
                        seat.seatStatus = seat.seatStatus.getRevertedStatus()
                        true
                    }

                    else -> {
                        false
                    }
                }

            }
        )
    }

    private fun List<BaseSeat>.isSeatNearWithOthers(seat : BaseSeat) : Boolean =
        this.find { seat.column + 1 == it.column || seat.column - 1 == it.column } != null

    private fun List<BaseSeat>.revertClickedStatuses(){
        this.map { it.seatStatus = SeatStatus.NOT_CLICKED }
    }
}
