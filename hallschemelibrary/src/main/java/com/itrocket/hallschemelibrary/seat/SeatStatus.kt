package com.itrocket.hallschemelibrary.seat

enum class SeatStatus {
    CLICKED,
    NOT_CLICKED,
    NOT_CLICKABLE;
}

fun SeatStatus.isClickable() = this == SeatStatus.CLICKED || this == SeatStatus.NOT_CLICKED
fun SeatStatus.getRevertedStatus() = if (this == SeatStatus.CLICKED) SeatStatus.NOT_CLICKED else SeatStatus.CLICKED
