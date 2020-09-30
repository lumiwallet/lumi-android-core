package com.lumiwallet.android.core.bitcoin.types

class OpSize private constructor(val size: Byte) {

    companion object {

        fun ofByte(byteValue: Byte): Byte {
            require(!(byteValue < 1 || byteValue > 0x4b)) {
                "Only one byte op size is supported."
            }
            return byteValue
        }

        fun ofInt(intValue: Int): Byte {
            val byteValue = intValue.toByte()
            require(!(byteValue < 1 || byteValue > 0x4b)) {
                "Only one byte op size is supported."
            }
            return byteValue
        }
    }

}
