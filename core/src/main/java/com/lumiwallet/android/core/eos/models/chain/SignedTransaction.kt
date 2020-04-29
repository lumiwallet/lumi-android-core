package com.lumiwallet.android.core.eos.models.chain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lumiwallet.android.core.eos.ec.EcDsa
import com.lumiwallet.android.core.eos.ec.EosPrivateKey
import com.lumiwallet.android.core.eos.models.types.EosByteWriter
import com.lumiwallet.android.core.eos.models.types.TypeChainId
import com.lumiwallet.android.core.utils.Sha256Hash
import java.util.*


class SignedTransaction : Transaction {

    @Expose
    @SerializedName("signatures")
    internal var signatures: MutableList<String> = mutableListOf()

    @Expose
    @SerializedName("ctx_free_data")
    var ctxFreeData: List<String> = ArrayList()

    val ctxFreeDataCount: Int
        get() = ctxFreeData.size


    constructor() : super()

    constructor(anotherTxn: SignedTransaction) : super(anotherTxn) {
        this.signatures = deepCopyOnlyContainer(anotherTxn.signatures)
    }

    fun getSignatures(): List<String>? = signatures

    fun putSignatures(signatures: MutableList<String>) {
        this.signatures = signatures
    }


    private fun getDigestForSignature(chainId: TypeChainId): ByteArray {
        val writer = EosByteWriter(255)
        writer.putBytes(chainId.bytes)
        pack(writer)
        if (ctxFreeData.isEmpty()) {
            writer.putBytes(Sha256Hash.ZERO_HASH.bytes)
        } else {
            //TODO not implemented
        }

        return Sha256Hash.hash(writer.toBytes())
    }

    fun sign(privateKey: EosPrivateKey, chainId: TypeChainId) {
        val signature = EcDsa.sign(getDigestForSignature(chainId), privateKey)
        this.signatures.add(signature.toString())
    }
}

