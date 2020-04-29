package com.lumiwallet.lumi_core.domain.eosSigning

import com.lumiwallet.android.core.eos.ec.EosPrivateKey
import com.lumiwallet.android.core.eos.models.chain.Action
import com.lumiwallet.android.core.eos.models.chain.PackedTransaction
import com.lumiwallet.android.core.eos.models.chain.SignedTransaction
import com.lumiwallet.android.core.eos.models.types.TypeChainId
import com.lumiwallet.lumi_core.domain.entity.SignedEosTransactionViewModel
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class SignEos @Inject constructor() {

    /**
     * To create "bin args" you can use this API method:
     * https://developers.eos.io/manuals/eos/latest/nodeos/plugins/chain_api_plugin/api-reference/index#operation/abi_json_to_bin
     * */

    companion object {
        const val SHORT_NAME = "EOS"
        const val ACTION_TRANSFER = "transfer"
        const val CODE_EOSIO_TOKEN = "eosio.token"
    }

    operator fun invoke(
        address: String,
        amount: String,
        memo: String,
        actor: String,
        permission: String,
        expiration: String,
        bin: String,
        blockPrefix: String,
        blockNum: String,
        chainId: String,
        privateKey: String
    ): Single<SignedEosTransactionViewModel> = Single.create { emitter ->
        val transaction = createTransaction(
            dataAsHex = bin,
            permissions = arrayOf("$actor@$permission"),
            blockNum = blockNum.toInt(),
            blockPrefix = blockPrefix.toLong(),
            expiration = expiration
        )
        transaction.sign(EosPrivateKey(privateKey), TypeChainId(chainId))
        val packedTransaction = PackedTransaction(transaction)
        val txViewModel = SignedEosTransactionViewModel(
            packedTx = packedTransaction.toString(),
            signatures = transaction.getSignatures()!!.joinToString (separator = "\n") { it },
            from = actor,
            to = address,
            memo = memo,
            value = "$amount $SHORT_NAME"
        )
        emitter.onSuccess(txViewModel)
    }

    private fun createTransaction(
        dataAsHex: String,
        permissions: Array<String>,
        blockNum: Int,
        blockPrefix: Long,
        expiration: String
    ): SignedTransaction {

        val action = Action(CODE_EOSIO_TOKEN, ACTION_TRANSFER).apply {
            setAuthorization(permissions)
            setData(dataAsHex)
        }

        return SignedTransaction().apply {
            addAction(action)
            putSignatures(ArrayList())
            setBlockNum(blockNum)
            setBlockPrefix(blockPrefix)
            this.expiration = expiration
        }
    }
}