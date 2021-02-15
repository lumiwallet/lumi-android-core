package com.lumiwallet.android.core.bitcoinVault.transaction

import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey

class UnspentOutput(
    val txHash: String,
    var txOutputN: Int,
    val script: String,
    var value: Long,
    val privateKey: PrivateKey
)