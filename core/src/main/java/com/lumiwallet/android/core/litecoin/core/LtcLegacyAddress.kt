package com.lumiwallet.android.core.litecoin.core


import com.google.common.base.Objects
import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.litecoin.script.ScriptType
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.btc_based.core.Address
import com.lumiwallet.android.core.utils.btc_based.core.AddressFormatException
import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

class LtcLegacyAddress @Throws(AddressFormatException::class) private constructor(
    params: NetworkParameters,
    val p2sh: Boolean,
    hash160: ByteArray
) : Address(params, hash160) {

    companion object {

        @Throws(AddressFormatException::class)
        fun fromPubKeyHash(params: NetworkParameters, hash160: ByteArray): LtcLegacyAddress {
            return LtcLegacyAddress(params, false, hash160)
        }

        fun fromKey(params: NetworkParameters, key: ECKey): LtcLegacyAddress {
            return fromPubKeyHash(params, key.pubKeyHash)
        }
    }

    val version: Int
        get() = if (p2sh) params.p2SHHeader else params.addressHeader

    override val hash: ByteArray
        get() = bytes

    val outputScriptType: ScriptType
        get() = if (p2sh) ScriptType.P2SH else ScriptType.P2PKH

    init {
        if (hash160.size != 20)
            throw AddressFormatException.InvalidDataLength(
                "Legacy addresses are 20 byte (160 bit) hashes, but got: " + hash160.size
            )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toBase58(): String = Base58.encodeChecked(version, bytes)

    override fun equals(o: Any?): Boolean {
        if (this === o)
            return true
        if (o == null || javaClass != o.javaClass)
            return false
        val other = o as LtcLegacyAddress?
        return super.equals(other) && this.p2sh == other.p2sh
    }

    override fun hashCode(): Int = Objects.hashCode(super.hashCode(), p2sh)

    override fun toString(): String = toBase58()

    @Throws(CloneNotSupportedException::class)
    override fun clone(): LtcLegacyAddress = super.clone() as LtcLegacyAddress

}