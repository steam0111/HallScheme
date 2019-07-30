package com.itrocket.hallschemelibrary.legend

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Legend(val text : String, val drawableResId : Int): Serializable, Parcelable