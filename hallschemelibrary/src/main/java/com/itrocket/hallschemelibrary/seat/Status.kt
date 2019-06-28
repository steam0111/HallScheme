package com.itrocket.hallschemelibrary.seat

enum class Status {
    CLICKED, NOT_CLICKED, NOT_CLICKABLE;
}

fun Status.isClickable() = this == Status.CLICKED || this == Status.NOT_CLICKED
fun Status.getRevertedStatus() = if (this == Status.CLICKED) Status.NOT_CLICKED else Status.CLICKED
