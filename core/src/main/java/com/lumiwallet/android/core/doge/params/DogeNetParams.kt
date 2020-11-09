package com.lumiwallet.android.core.doge.params


import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

object DogeNetParams : NetworkParameters() {

    override val dumpedPrivateKeyHeader = 158
    override val addressHeader = 30
    override val p2SHHeader = 22
    override val packetMagic = -0x3f3f3f40L
    override val id = ID_DOGENET
}
