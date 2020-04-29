package com.lumiwallet.lumi_core.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InputViewModel(
    val address: String,
    val amount: String,
    val script: String,
    val txHash: String
): Parcelable