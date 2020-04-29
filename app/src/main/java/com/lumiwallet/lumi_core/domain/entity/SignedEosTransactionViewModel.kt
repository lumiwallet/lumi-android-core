package com.lumiwallet.lumi_core.domain.entity

class SignedEosTransactionViewModel (
    val packedTx: String,
    val signatures: String,
    val from: String,
    val to: String,
    val memo: String,
    val value: String
)