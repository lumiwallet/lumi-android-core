package com.lumiwallet.android.core.eos.models.types

interface EosType {
    class InsufficientBytesException : Exception() {
        companion object {
            private const val serialVersionUID = 1L
        }
    }

    interface Packer {
        fun pack(paramWriter: Writer)
    }

    interface Reader {

        @Throws(InsufficientBytesException::class)
        fun get(): Byte

        @Throws(InsufficientBytesException::class)
        fun getBytes(paramInt: Int): ByteArray

        @Throws(InsufficientBytesException::class)
        fun getIntLE(): Int

        @Throws(InsufficientBytesException::class)
        fun getLongLE(): Long

        @Throws(InsufficientBytesException::class)
        fun getShortLE(): Int

        @Throws(InsufficientBytesException::class)
        fun getString(): String

        @Throws(InsufficientBytesException::class)
        fun getVariableUint(): Long

    }

    interface Unpacker {
        @Throws(InsufficientBytesException::class)
        fun unpack(paramReader: Reader)
    }

    interface Writer {
        fun length(): Int

        fun put(paramByte: Byte)

        fun putBytes(paramArrayOfByte: ByteArray)

        fun putCollection(paramCollection: Collection<Packer>)

        fun putIntLE(paramInt: Int)

        fun putLongLE(paramLong: Long)

        fun putShortLE(paramShort: Short)

        fun putString(paramString: String)

        fun putVariableUInt(paramLong: Long)

        fun toBytes(): ByteArray
    }
}