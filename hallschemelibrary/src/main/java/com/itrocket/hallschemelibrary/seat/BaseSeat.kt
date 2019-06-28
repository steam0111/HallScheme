package com.itrocket.hallschemelibrary.seat

import java.io.Serializable

abstract class BaseSeat(
    var column: Int = 0,
    var row: Int = 0,
    var status : Status = Status.NOT_CLICKED) : IBaseSeat, Serializable