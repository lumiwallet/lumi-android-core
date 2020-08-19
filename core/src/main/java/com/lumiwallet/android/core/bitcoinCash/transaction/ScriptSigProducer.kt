package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.bitcoinCash.constant.SigHashType
import com.lumiwallet.android.core.bitcoinCash.core.PrivateKey
import com.lumiwallet.android.core.bitcoinCash.types.OpSize
import com.lumiwallet.android.core.bitcoinCash.util.ByteBuffer

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
