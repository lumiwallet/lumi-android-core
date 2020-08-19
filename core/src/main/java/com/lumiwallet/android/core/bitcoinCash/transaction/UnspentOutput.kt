package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.bitcoinCash.core.PrivateKey

class UnspentOutput(
    val txHash: String,
    var txOutputN: Int,
    val script: String,
    var value: Long,
    val privateKey: PrivateKey
)