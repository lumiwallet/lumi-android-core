package com.lumiwallet.android.core.bitcoin.types

class OpSize private constructor(val size: Byte) {

    companion object {

        fun ofByte(byteValue: Byte): OpSize {
            require(!(byteValue < 1 || byteValue > 0x4b)) {
                "Only one byte op size is supported."
            }
            return OpSize(byteValue)
        }

        fun ofInt(intValue: Int): OpSize = ofByte(intValue.toByte())
    }
}
