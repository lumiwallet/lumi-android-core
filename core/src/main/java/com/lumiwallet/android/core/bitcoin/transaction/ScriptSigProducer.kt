package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.OpCodes
import com.lumiwallet.android.core.bitcoin.constant.SigHashType
import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.script.ScriptType
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer

object ScriptSigProducer {

    internal fun produceScriptSig(
        sigHash: ByteArray,
        key: PrivateKey,
        scriptType: ScriptType
    ): ByteArray = when (scriptType) {
        ScriptType.P2WPKH -> byteArrayOf()
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
        ScriptType.P2SH -> {
            ByteBuffer().apply {
                val pubKeyHash = key.key.pubKeyHash
                append(OpCodes.FALSE)
                append(pubKeyHash.size.toByte())
                append(*pubKeyHash)
                putFirst(OpSize.ofInt(this.size))
            }
                .bytes
        }
        else -> throw IllegalArgumentException()
    }

}
