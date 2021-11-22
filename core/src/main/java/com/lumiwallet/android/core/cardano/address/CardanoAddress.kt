package com.lumiwallet.android.core.cardano.address

import com.lumiwallet.android.core.utils.blake2b224

class CardanoAddress(
    private val prefix: Byte,
    private val payload: ByteArray,
    private val type: AddressType
) {
    var address: String = ""
        get() {
            if (field.isEmpty()) {
                field = when(type) {
                    AddressType.DELEGATE -> {
                        cardanoMainNetHRP + cardanoPrefixSplitSimbol + Bech32.encode(byteArrayOf(prefix) + payload, cardanoMainNetHRP)
                    }
                    AddressType.STAKE -> {
                        cardanoMainNetStakeHRP + cardanoPrefixSplitSimbol + Bech32.encode(byteArrayOf(prefix) + payload, cardanoMainNetStakeHRP)
                    }
                }
            }
            return field
        }

    enum class AddressType {
        STAKE, DELEGATE
    }

    companion object {
        private const val cardanoMainNetTag: Byte = 0x01
        const val cardanoMainNetHRP: String = "addr"
        const val cardanoMainNetStakeHRP: String = "stake"
        const val cardanoPrefixSplitSimbol: String = "1"

        private const val rewardAccountKey = 0b11100000
        private const val baseAddressKeyKey = 0b00000000


        fun fromPublicKeys(
            publicKey: ByteArray,
            stakePublicKey: ByteArray
        ): CardanoAddress {
            return CardanoAddress(
                (baseAddressKeyKey + cardanoMainNetTag).toByte(),
                publicKey.blake2b224() + stakePublicKey.blake2b224(),
                AddressType.DELEGATE
            )
        }

        fun stakeAddress(
            stakePublicKey: ByteArray
        ): CardanoAddress {
            return CardanoAddress(
                (rewardAccountKey + cardanoMainNetTag).toByte(),
                stakePublicKey.blake2b224(),
                AddressType.STAKE
            )
        }
    }

    override fun toString(): String = address

}