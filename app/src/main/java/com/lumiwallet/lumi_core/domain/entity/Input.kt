package com.lumiwallet.lumi_core.domain.entity

class Input (
    var txHash: String,
    var txOutputN: Int,
    var script: String,
    var value: Long,
    var address: String,
    var keyAsWif: String
)