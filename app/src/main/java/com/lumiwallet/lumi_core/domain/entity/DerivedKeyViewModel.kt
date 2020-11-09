package com.lumiwallet.lumi_core.domain.entity

class DerivedKeyViewModel (
    val sequence: Int = 0,
    val btcAddress: String = "",
    val eosAddress: String = "",
    val dogeAddress: String = "",
    val pubKey: String,
    val privKey: String
) {
    override fun toString(): String = StringBuilder()
        .appendln("sequence: $sequence")
        .appendln("btc address: $btcAddress")
        .appendln("c address: $dogeAddress")
        .appendln("eos address: $eosAddress")
        .appendln("public key: $pubKey")
        .appendln("private key: $privKey")
        .toString()
}