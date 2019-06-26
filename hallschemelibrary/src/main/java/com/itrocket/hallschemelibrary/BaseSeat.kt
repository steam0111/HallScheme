package com.itrocket.hallschemelibrary

import java.io.Serializable

abstract class BaseSeat(var column: Int = 0, var row: Int = 0) : IBaseSeat, Serializable