package com.itrocket.hallschemelibrary.screen

import java.io.Serializable

data class Screen(
    val backgroundColorInt: Int,
    val textColorInt: Int,
    val textSize: Float,
    val text: String
) : Serializable