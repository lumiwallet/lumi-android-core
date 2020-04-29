package com.lumiwallet.android.core.bitcoin.params


import com.lumiwallet.android.core.bitcoin.core.NetworkParameters

object DogeNetParams : NetworkParameters() {
    init {
        dumpedPrivateKeyHeader = 158 //This is always addressHeader + 128
        addressHeader = 30
        p2SHHeader = 22
        packetMagic = -0x3f3f3f40
        id = ID_DOGENET
    }

}
