package com.lumiwallet.android.core.utils.btc_based.core

import com.lumiwallet.android.core.doge.params.DogeNetParams

abstract class NetworkParameters {

    companion object {

        const val ID_DOGENET = "org.dogecoin.production"
        const val ID_MAINNET = "org.bitcoin.production"
        const val ID_LITECOIN_MAINNET = "org.litecoin.production"

        /** Returns the network parameters for the given string ID or NULL if not recognized.  */
        fun fromID(id: String): NetworkParameters? = when(id) {
            ID_DOGENET -> DogeNetParams
            ID_MAINNET -> com.lumiwallet.android.core.bitcoin.params.MainNetParams
            ID_LITECOIN_MAINNET -> com.lumiwallet.android.core.litecoin.params.LitecoinMainNetParams
            else -> null
        }
    }

    abstract val packetMagic: Long

    abstract val id: String

    abstract val addressHeader: Int
    abstract val p2SHHeader: Int
    abstract val dumpedPrivateKeyHeader: Int

    open val segwitAddressHrp: String? = null

    open val bip32HeaderP2PKHpub: Int = 0x0488b21e
    open val bip32HeaderP2PKHpriv: Int = 0x0488ade4
    open val bip32HeaderP2WPKHpub: Int = 0x04b24746
    open val bip32HeaderP2WPKHpriv: Int = 0x04b2430c

}