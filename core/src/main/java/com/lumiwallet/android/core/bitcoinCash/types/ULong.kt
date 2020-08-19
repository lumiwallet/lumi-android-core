package com.lumiwallet.android.core.bitcoinCash.types

import org.bouncycastle.util.encoders.Hex

class ULong private constructor(value: Long) {

    companion object {

        fun of(value: Long): ULong = ULong(value)
    }

    private val litEndBytes: ByteArray = byteArrayOf(
        value.toByte(),
        (value shr 8).toByte(),
        (value shr 16).toByte(),
        (value shr 24).toByte(),
        (value shr 32).toByte(),
        (value shr 40).toByte(),
        (value shr 48).toByte(),
        (value shr 56).toByte()
    )

    fun asLitEndBytes(): ByteArray = litEndBytes

    override fun toString(): String = Hex.toHexString(litEndBytes)


}
