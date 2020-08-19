package com.lumiwallet.android.core.utils

import com.lumiwallet.android.core.eos.utils.HexUtils

val ByteArray.hex : String
    get() = HexUtils.toHex(this)

fun String.safeToByteArray(): ByteArray = try {
    HexUtils.toBytes(this)
} catch(e: Exception) {
    byteArrayOf()
}