package com.lumiwallet.android.core.doge.transaction

import com.lumiwallet.android.core.doge.constant.SigHashType
import com.lumiwallet.android.core.doge.script.ScriptType
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey
import com.lumiwallet.android.core.utils.btc_based.types.OpSize

object ScriptSigProducer {

    internal fun produceScriptSig(
        sigHash: ByteArray,
        key: PrivateKey,
        scriptType: ScriptType
    ): ByteArray = when (scriptType) {
        ScriptType.P2PKH -> {
            ByteBuffer().apply {
                val publicKey = key.publicKey
                append(*key.sign(sigHash))
                append(SigHashType.ALL.asByte())
                putFirst(OpSize.ofInt(this.size))
                append(OpSize.ofInt(publicKey.size))
                append(*publicKey)
            }
                .bytes
        }
    }

}
