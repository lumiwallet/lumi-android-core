package com.lumiwallet.lumi_core.domain.entity

import com.lumiwallet.android.core.bitcoin.transaction.UnspentOutput

class Input (
    var unspentOutput: UnspentOutput,
    var address: String,
    var keyAsWif: String
)