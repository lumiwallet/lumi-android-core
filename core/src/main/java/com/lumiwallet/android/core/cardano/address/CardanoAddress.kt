package com.lumiwallet.android.core.cardano.address

import com.lumiwallet.android.core.utils.blake2b224

class CardanoAddress(
    private val prefix: Byte,
    private val payload: ByteArray

) {
    var address: String = ""


    companion object {
        private const val cardanoMainNetTag: Byte = 0x01
        const val cardanoMainNetHRP: String = "addr"
        const val cardanoMainNetStakeHRP: String = "stake"
        const val cardanoPrefixSplitSimbol: String = "1"

        private const val baseAddressKeyKey = 0b00000000

        fun fromPublicKeys(
            publicKey: ByteArray,
            stakePublicKey: ByteArray
        ): CardanoAddress {
            return CardanoAddress(
                (baseAddressKeyKey + cardanoMainNetTag).toByte(),
                publicKey.blake2b224() + stakePublicKey.blake2b224()
            )
        }
    }

    override fun toString(): String =
        cardanoMainNetHRP + cardanoPrefixSplitSimbol + Bech32.encode(payload + byteArrayOf(prefix), cardanoMainNetHRP)

}