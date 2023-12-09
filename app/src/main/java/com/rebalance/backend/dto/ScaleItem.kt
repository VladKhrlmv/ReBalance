package com.rebalance.backend.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Item used for changing scales on personal screen (vertical navigation) **/
@Parcelize
data class ScaleItem(
    val type: String,
    val name: String
) : Parcelable
