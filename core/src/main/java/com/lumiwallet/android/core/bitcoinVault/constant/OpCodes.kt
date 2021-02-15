package com.lumiwallet.android.core.bitcoinVault.constant

import com.google.common.base.Preconditions

object OpCodes {
    val FALSE = 0x00.toByte()
    val DUP = 0x76.toByte()
    val HASH160 = 0xA9.toByte()
    val EQUAL = 0x87.toByte()
    val EQUALVERIFY = 0x88.toByte()
    val CHECKSIG = 0xAC.toByte()

    val OP_0 = 0x00 // push empty vector
    val OP_1NEGATE = 0x4f
    val OP_1 = 0x51

    fun encodeToOpN(value: Int): Int {
        Preconditions.checkArgument(value >= -1 && value <= 16, "encodeToOpN called for $value which we cannot encode in an opcode.")
        return when (value) {
            0 -> OP_0
            -1 -> OP_1NEGATE
            else -> value - 1 + OP_1
        }
    }
}