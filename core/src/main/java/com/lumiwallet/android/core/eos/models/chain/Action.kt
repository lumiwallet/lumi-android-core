package com.lumiwallet.android.core.eos.models.chain


import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lumiwallet.android.core.eos.models.types.EosType
import com.lumiwallet.android.core.eos.models.types.TypeAccountName
import com.lumiwallet.android.core.eos.models.types.TypeActionName
import com.lumiwallet.android.core.eos.models.types.TypePermissionLevel
import com.lumiwallet.android.core.eos.utils.HexUtils
import java.util.*

class Action constructor(
        account: String,
        name: String,
        authorization: TypePermissionLevel? = null,
        data: String? = null
) : EosType.Packer {

    @Expose
    @SerializedName("account")
    private var account: TypeAccountName

    @Expose
    @SerializedName("name")
    private var name: TypeActionName

    @Expose
    @SerializedName("authorization")
    private var authorization: MutableList<TypePermissionLevel>

    @Expose
    @SerializedName("data")
    private var data: JsonElement? = null

    init {
        this.account = TypeAccountName(account)
        this.name = TypeActionName(name)
        this.authorization = ArrayList()
        authorization?.let {
            this.authorization.add(it)
        }
        data?.let {
            this.data = JsonPrimitive(it)
        }

    }

    fun getAccount(): String {
        return account.toString()
    }

    fun setAccount(account: String) {
        this.account = TypeAccountName(account)
    }

    fun getName(): String {
        return name.toString()
    }

    fun setName(name: String) {
        this.name = TypeActionName(name)
    }

    fun getAuthorization(): List<TypePermissionLevel>? {
        return authorization
    }

    fun setAuthorization(authorization: MutableList<TypePermissionLevel>) {
        this.authorization = authorization
    }

    fun setAuthorization(authorization: Array<TypePermissionLevel>) {
        this.authorization.addAll(Arrays.asList(*authorization))
    }

    fun setAuthorization(accountWithPermLevel: Array<String>?) {
        if (null == accountWithPermLevel) {
            return
        }

        for (permissionStr in accountWithPermLevel) {
            val split = permissionStr.split("@".toRegex(), 2).toTypedArray()
            authorization.add(TypePermissionLevel(split[0], split[1]))
        }
    }

    fun setData(data: String) {
        this.data = JsonPrimitive(data)
    }

    override fun pack(writer: EosType.Writer) {
        account.pack(writer)
        name.pack(writer)

        writer.putCollection(authorization)

        if (null != data) {
            val dataAsBytes = HexUtils.toBytes(data!!.asString)
            writer.putVariableUInt(dataAsBytes.size.toLong())
            writer.putBytes(dataAsBytes)
        } else {
            writer.putVariableUInt(0)
        }
    }
}