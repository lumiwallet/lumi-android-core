package com.lumiwallet.android.core.eos.models.chain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lumiwallet.android.core.eos.models.types.EosType
import java.util.*

open class Transaction : TransactionHeader {

    @Expose
    @SerializedName("context_free_actions")
    private var contextFreeActions = ArrayList<Action>()

    @Expose
    @SerializedName("actions")
    private var actions: MutableList<Action> = mutableListOf()

    @Expose
    @SerializedName("transaction_extensions")
    private var transactionExtensions = ArrayList<String>()

    val contextFreeActionCount: Int
        get() = actions.size

    constructor() : super() {}


    constructor(other: Transaction) : super(other) {
        this.contextFreeActions = deepCopyOnlyContainer(other.contextFreeActions) as ArrayList<Action>
        this.actions = deepCopyOnlyContainer(other.actions)
        this.transactionExtensions = other.transactionExtensions
    }

    fun addAction(msg: Action) {
        actions.add(msg)
    }

    fun getActions(): List<Action> = actions

    fun setActions(actions: MutableList<Action>) {
        this.actions = actions
    }

    internal fun <T> deepCopyOnlyContainer(srcList: MutableList<T>): MutableList<T> {

        val newList = ArrayList<T>(srcList.size)
        newList.addAll(srcList)

        return newList
    }

    override fun pack(writer: EosType.Writer) {
        super.pack(writer)
        writer.putCollection(contextFreeActions)
        writer.putCollection(actions)
        //writer.putCollection(transactionExtensions);
        writer.putVariableUInt(transactionExtensions.size.toLong())
        if (transactionExtensions.size > 0) {
            // TODO not implemented
        }
    }
}