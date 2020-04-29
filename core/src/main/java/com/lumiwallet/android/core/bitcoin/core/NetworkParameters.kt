package com.lumiwallet.android.core.bitcoin.core


import com.google.common.base.Objects
import com.lumiwallet.android.core.bitcoin.params.MainNetParams

abstract class NetworkParameters {

    companion object {

        const val ID_DOGENET = "org.dogecoin.production"
        const val ID_MAINNET = "org.bitcoin.production"

        /** Returns the network parameters for the given string ID or NULL if not recognized.  */
        fun fromID(id: String): NetworkParameters? {
            return if (id == ID_MAINNET) {
                MainNetParams
            } else {
                null
            }
        }
    }

    protected var packetMagic: Long = 0  // Indicates message origin network and is used to seek to the next message when stream state is unknown.

    var addressHeader: Int = 0
        protected set
    var p2SHHeader: Int = 0
        protected set
    var dumpedPrivateKeyHeader: Int = 0
        protected set
    var segwitAddressHrp: String? = null
        protected set
    var bip32HeaderP2PKHpub: Int = 0
        protected set
    var bip32HeaderP2PKHpriv: Int = 0
        protected set
    var bip32HeaderP2WPKHpub: Int = 0
        protected set
    var bip32HeaderP2WPKHpriv: Int = 0
        protected set

    var id: String = ""
        protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other == null || javaClass != other.javaClass) false else id == (other as NetworkParameters).id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

}