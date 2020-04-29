package com.lumiwallet.android.core.eos.ec

import com.google.common.base.Preconditions
import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.utils.Sha256Hash
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.math.ec.ECAlgorithms
import java.math.BigInteger
import java.util.*


object EcDsa {

    class SigChecker internal constructor(
            hash: ByteArray,
            private var privKey: BigInteger
    ) {
        internal var e: BigInteger = BigInteger(1, hash)

        internal var r: BigInteger = BigInteger.ONE
        internal var s: BigInteger = BigInteger.ONE

        internal fun checkSignature(
                curveParam: X9ECParameters,
                k: BigInteger
        ): Boolean {


            val ecPoint = ECAlgorithms.referenceMultiply(curveParam.g, k).normalize()
            if (ecPoint.isInfinity) return false

            r = ecPoint.xCoord.toBigInteger().mod(curveParam.n)
            if (r.signum() == 0) return false


            s = k.modInverse(curveParam.n)
                    .multiply(e.add(privKey.multiply(r)))
                    .mod(curveParam.n)

            return s.signum() != 0

        }

        fun isRSEachLength(length: Int): Boolean =
                r.toByteArray().size == length && s.toByteArray().size == length
    }

    fun sign(hash: ByteArray, key: EosPrivateKey): EcSignature {
        val privAsBI = key.asBigInteger
        val checker = SigChecker(hash, privAsBI)
        val curveParam = key.curveParam

        var nonce = 0

        val calculator = HMacDSAKCalculator(SHA256Digest())

        while (true) {
            val noncedHash = if (nonce > 0){
                Sha256Hash.hash(hash, BigInteger.valueOf(nonce.toLong()).toByteArray())
            } else hash

            calculator.init(ECKey.SECP256_K1_CURVE_PARAMS.n, privAsBI, noncedHash)
            checker.checkSignature(curveParam, calculator.nextK())

            if (checker.s > (curveParam.n.shiftRight(1))) {
                checker.s = curveParam.n.subtract(checker.s)
            }

            if (checker.isRSEachLength(32)) {
                break
            }
            nonce++
        }

        val signature = EcSignature(checker.r, checker.s, curveParam)

        val pubKey = key.publicKey

        for (i in 0..3) {
            val recovered = recoverPubKey(curveParam, hash, signature, i)
            if (pubKey == recovered) {
                signature.setRecid(i)
                break
            }
        }

        check(signature.recId >= 0) { "could not find recid. Was this data signed with this key?" }
        return signature
    }

    fun recoverPubKey(messageSigned: ByteArray, signature: EcSignature): EosPublicKey? {
        return recoverPubKey(signature.curveParam, messageSigned, signature, signature.recId)
    }

    private fun recoverPubKey(
            curveParam: X9ECParameters,
            messageSigned: ByteArray,
            signature: EcSignature,
            recId: Int
    ): EosPublicKey? {

        Preconditions.checkArgument(recId >= 0, "recId must be positive")
        Preconditions.checkArgument(signature.r >= BigInteger.ZERO, "r must be positive")
        Preconditions.checkArgument(signature.s >= BigInteger.ZERO, "s must be positive")
        Preconditions.checkNotNull(messageSigned)

        val n = curveParam.n
        val i = BigInteger.valueOf(recId.toLong() / 2)
        val x = signature.r.add(i.multiply(n))

        if (x >= curveParam.getQ()) return null

        val r = EcTools.decompressKey(curveParam, x, recId and 1 == 1)

        if (!r.multiply(n).isInfinity) return null

        val e = BigInteger(1, messageSigned)
        val eInv = BigInteger.ZERO.subtract(e).mod(n)
        val rInv = signature.r.modInverse(n)
        val srInv = rInv.multiply(signature.s).mod(n)
        val eInvrInv = rInv.multiply(eInv).mod(n)

        val q = ECAlgorithms.sumOfTwoMultiplies(curveParam.g, eInvrInv, r, srInv)

        return EosPublicKey(q.getEncoded(true))
    }


    private fun isSignerOf(
            curveParam: X9ECParameters,
            messageSigned: ByteArray,
            recId: Int,
            sig: EcSignature,
            pubKeyBytes: ByteArray
    ): Boolean {
        Preconditions.checkArgument(recId >= 0, "recId must be positive")
        Preconditions.checkArgument(sig.r >= BigInteger.ZERO, "r must be positive")
        Preconditions.checkArgument(sig.s >= BigInteger.ZERO, "s must be positive")
        Preconditions.checkNotNull(messageSigned)

        val n = curveParam.n
        val i = BigInteger.valueOf(recId.toLong() / 2)
        val x = sig.r.add(i.multiply(n))

        if (x >= curveParam.getQ()) return false

        val r = EcTools.decompressKey(curveParam, x, recId and 1 == 1)

        if (!r.multiply(n).isInfinity) return false

        val e = BigInteger(1, messageSigned)

        val eInv = BigInteger.ZERO.subtract(e).mod(n)
        val rInv = sig.r.modInverse(n)
        val srInv = rInv.multiply(sig.s).mod(n)
        val eInvrInv = rInv.multiply(eInv).mod(n)

        val q = ECAlgorithms.sumOfTwoMultiplies(curveParam.g, eInvrInv, r, srInv)

        val recoveredPub = q.getEncoded(true)

        return Arrays.equals(recoveredPub, pubKeyBytes)
    }
}