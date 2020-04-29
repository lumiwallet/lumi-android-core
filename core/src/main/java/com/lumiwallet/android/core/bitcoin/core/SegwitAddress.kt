package com.lumiwallet.android.core.bitcoin.core


import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.lumiwallet.android.core.bitcoin.constant.OpCodes
import com.lumiwallet.android.core.bitcoin.core.SegwitAddressUtils.convertBits
import com.lumiwallet.android.core.bitcoin.script.ScriptType
import com.lumiwallet.android.core.crypto.ECKey
import kotlin.experimental.and

class SegwitAddress @Throws(AddressFormatException::class)
private constructor(
        params: NetworkParameters,
        data: ByteArray
) : Address(params, data) {

    companion object {
        const val WITNESS_PROGRAM_LENGTH_PKH = 20
        const val WITNESS_PROGRAM_LENGTH_SH = 32
        const val WITNESS_PROGRAM_MIN_LENGTH = 2
        const val WITNESS_PROGRAM_MAX_LENGTH = 40

        @Throws(AddressFormatException::class)
        private fun encode(witnessVersion: Int, witnessProgram: ByteArray): ByteArray {
            val convertedProgram = convertBits(witnessProgram, 0, witnessProgram.size, 8, 5, true)
            val bytes = ByteArray(1 + convertedProgram.size)
            bytes[0] = (OpCodes.encodeToOpN(witnessVersion) and 0xff).toByte()
            System.arraycopy(convertedProgram, 0, bytes, 1, convertedProgram.size)
            return bytes
        }

        fun fromHash(
                params: NetworkParameters,
                hash: ByteArray
        ): SegwitAddress = SegwitAddress(params, 0, hash)

        fun fromKey(
                params: NetworkParameters,
                key: ECKey
        ): SegwitAddress {
            checkArgument(key.isCompressed, "only compressed keys allowed")
            return fromHash(params, key.pubKeyHash!!)
        }
    }

    @Throws(AddressFormatException::class)
    private constructor(
            params: NetworkParameters,
            witnessVersion: Int,
            witnessProgram: ByteArray
    ) : this(params, encode(witnessVersion, witnessProgram))

    init {
        if (data.isEmpty())
            throw AddressFormatException.InvalidDataLength("Zero data found")
        if (witnessVersion < 0 || witnessVersion > 16)
            throw AddressFormatException("Invalid script version: $witnessVersion")
        if (witnessProgram.size < WITNESS_PROGRAM_MIN_LENGTH || witnessProgram.size > WITNESS_PROGRAM_MAX_LENGTH)
            throw AddressFormatException.InvalidDataLength("Invalid length: ${witnessProgram.size}")
        if (witnessVersion == 0 && witnessProgram.size != WITNESS_PROGRAM_LENGTH_PKH
                && witnessProgram.size != WITNESS_PROGRAM_LENGTH_SH)
            throw AddressFormatException.InvalidDataLength(
                    "Invalid length for address version 0: ${witnessProgram.size}")
    }

    val witnessVersion: Int
        get() = (bytes[0] and 0xff.toByte()).toInt()

    val witnessProgram: ByteArray
        get() = convertBits(bytes, 1, bytes.size - 1, 5, 8, false)

    override val hash: ByteArray
        get() = witnessProgram

    override val outputScriptType: ScriptType
        get() {
            val version = witnessVersion
            checkState(version == 0)
            val programLength = witnessProgram.size
            if (programLength == WITNESS_PROGRAM_LENGTH_PKH)
                return ScriptType.P2WPKH
            if (programLength == WITNESS_PROGRAM_LENGTH_SH)
                return ScriptType.P2WSH
            throw IllegalStateException("Cannot happen.")
        }



    override fun toString(): String = toBech32()

    fun toBech32(): String = Bech32.encode(params.segwitAddressHrp, bytes)

}
