package com.lumiwallet.android.core.bitcoinCash.params


import com.lumiwallet.android.core.bitcoinCash.core.NetworkParameters

object MainNetParams : NetworkParameters() {
    init {
        dumpedPrivateKeyHeader = 128
        addressHeader = 0
        p2SHHeader = 5
        segwitAddressHrp = "q"
        packetMagic = 0xf9beb4d9L
        bip32HeaderP2PKHpub = 0x0488b21e // The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderP2PKHpriv = 0x0488ade4 // The 4 byte header that serializes in base58 to "xprv"
        bip32HeaderP2WPKHpub = 0x04b24746 // The 4 byte header that serializes in base58 to "zpub".
        bip32HeaderP2WPKHpriv = 0x04b2430c // The 4 byte header that serializes in base58 to "zprv"
        id = ID_MAINNET
    }

}