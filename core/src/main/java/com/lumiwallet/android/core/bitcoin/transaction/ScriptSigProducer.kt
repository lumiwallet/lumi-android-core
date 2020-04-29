package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.OpCodes
import com.lumiwallet.android.core.bitcoin.constant.SigHashType
import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer
import com.lumiwallet.android.core.crypto.Ripemd160
import com.lumiwallet.android.core.utils.Sha256Hash

object ScriptSigProducer {

    internal fun produceScriptSig(segwit: Boolean, sigHash: ByteArray, key: PrivateKey): ByteArray {
        if (segwit) {

            val result = ByteBuffer()

            result.append(OpCodes.FALSE)
            result.append(0x14.toByte()) //from size
            result.append(*Ripemd160.from(Sha256Hash.hash(key.publicKey)))
            result.putFirst(OpSize.ofInt(result.size()).size) //PUSH DATA

            return result.bytes()

        } else {

            val result = ByteBuffer()

            result.append(*key.sign(sigHash))
            result.append(SigHashType.ALL.asByte())

            result.putFirst(OpSize.ofInt(result.size()).size)

            val publicKey = key.publicKey
            result.append(OpSize.ofInt(publicKey.size).size)
            result.append(*publicKey)

            return result.bytes()

        }
    }

}
