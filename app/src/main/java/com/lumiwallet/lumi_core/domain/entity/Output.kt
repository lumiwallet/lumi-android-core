package com.lumiwallet.lumi_core.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Output(
    var address: String,
    var amount: Long
): Parcelable