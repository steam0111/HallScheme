package com.itrocket.hallschemelibrary.seat

abstract class BaseSeat(
    var column: Int = 0,
    var row: Int = 0,
    var seatStatus : SeatStatus = SeatStatus.NOT_CLICKED) : IBaseSeat {

}