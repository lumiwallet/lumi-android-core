package com.lumiwallet.android.core.doge.core

import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.doge.script.ScriptType
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.btc_based.core.Address
import com.lumiwallet.android.core.utils.btc_based.core.AddressFormatException
import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

class LegacyAddress @Throws(AddressFormatException::class) private constructor(
    params: NetworkParameters,
    hash160: ByteArray
) : Address(params, hash160) {

    companion object {

        @Throws(AddressFormatException::class)
        fun fromPubKeyHash(params: NetworkParameters, hash160: ByteArray): LegacyAddress {
            return LegacyAddress(params, hash160)
        }

        fun fromKey(params: NetworkParameters, key: ECKey): LegacyAddress {
            return fromPubKeyHash(params, key.pubKeyHash)
        }
    }

    val version: Int
        get() = params.addressHeader

    override val hash: ByteArray
        get() = bytes

    val outputScriptType: ScriptType
        get() = ScriptType.P2PKH

    init {
        if (hash160.size != 20)
            throw AddressFormatException.InvalidDataLength(
                "Legacy addresses are 20 byte (160 bit) hashes, but got: " + hash160.size
            )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toBase58(): String = Base58.encodeChecked(version, bytes)

    override fun toString(): String = toBase58()

    @Throws(CloneNotSupportedException::class)
    override fun clone(): LegacyAddress = super.clone() as LegacyAddress


}
