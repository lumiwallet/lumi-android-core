package com.lumiwallet.android.core.bitcoinVault.params

import com.lumiwallet.android.core.utils.btc_based.core.NetworkParameters

object BitcoinVaultParams : NetworkParameters() {

    override val dumpedPrivateKeyHeader = 2
    override val addressHeader = 0x4E
    override val p2SHHeader = 0x3C
    override val segwitAddressHrp = "royale"
    override val packetMagic = 0xf9beb4d9L
    override val id = ID_MAINNET
}