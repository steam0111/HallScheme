package com.itrocket.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.itrocket.hallschemelibrary.legend.Legend
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

        btnGetClickedCount.setOnClickListener {
            Toast.makeText(this, seatPlanView.getClickedSeats().size.toString(), Toast.LENGTH_SHORT)
                .show()
        }

        seatPlanView.drawSeatPlan(
            getSeatData(), // make data for seat
            true, //define zoomable or not
            getLegendData(), //make data for legend
            clickedRuleForClickableItems = { seat, seats ->
                seatValidationRules(seat, seats) //set validation rules for click on seats
            },
            seatGap = 1 //seat gap between chairs
        )
    }

    private fun seatValidationRules(seat: BaseSeat, seats: List<BaseSeat>): Boolean = when {
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

    private fun getLegendData(): List<Legend> {
        return listOf(
            Legend("legenda 1", R.drawable.ic_android_black_24dp),
            Legend("legenda 2", R.drawable.ic_android_red_24dp),
            Legend("legenda 3", R.drawable.ic_android_black_24dp),
            Legend("legenda 4", R.drawable.ic_android_red_24dp)
        )
    }

    private fun getSeatData(): List<BaseSeat> {

        val seatList = mutableListOf<BaseSeat>()

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

                } else {

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

        return seatList
    }

    private fun List<BaseSeat>.isSeatNearWithOthers(seat: BaseSeat): Boolean =
        this.find { seat.column + 1 == it.column || seat.column - 1 == it.column } != null

    private fun List<BaseSeat>.revertClickedStatuses() {
        this.map { it.seatStatus = SeatStatus.NOT_CLICKED }
    }
}
