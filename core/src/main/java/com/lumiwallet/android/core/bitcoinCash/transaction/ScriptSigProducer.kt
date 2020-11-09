package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.bitcoinCash.constant.SigHashType
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey
import com.lumiwallet.android.core.utils.btc_based.types.OpSize

object ScriptSigProducer {

    internal fun produceScriptSig(
        sigHash: ByteArray,
        key: PrivateKey
    ): ByteArray = ByteBuffer().apply {
        append(*key.sign(sigHash))
        append(SigHashType.ALL.asByte())
        putFirst(OpSize.ofInt(this.size))
        append(OpSize.ofInt(key.publicKey.size))
        append(*key.publicKey)
    }.bytes
}
