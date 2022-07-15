package com.lumiwallet.android.core.litecoin.params

import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

object LitecoinMainNetParams : NetworkParameters() {

    override val addressHeader = 48
    override val dumpedPrivateKeyHeader = 128 + addressHeader
    override val p2SHHeader = 50
    override val segwitAddressHrp = "ltc"
    override val packetMagic = 0xfbc0b6dbL
    override val id = ID_LITECOIN_MAINNET
}