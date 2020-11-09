package com.lumiwallet.android.core.bitcoinCash.params

import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

object MainNetParams : NetworkParameters() {

    override val dumpedPrivateKeyHeader = 128
    override val addressHeader = 0
    override val p2SHHeader = 5
    override val segwitAddressHrp = "bitcoincash"
    override val packetMagic = 0xf9beb4d9L
    override val id = ID_MAINNET
}