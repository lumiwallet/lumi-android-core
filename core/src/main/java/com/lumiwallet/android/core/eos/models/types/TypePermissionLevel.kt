package com.lumiwallet.android.core.eos.models.types

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TypePermissionLevel(accountName: String, permissionName: String) : EosType.Packer {

    @Expose
    @SerializedName("actor")
    private var actor: TypeAccountName

    @Expose
    @SerializedName("permission")
    private var permission: TypePermissionName

    var account: String
        get() = actor.toString()
        set(accountName) {
            actor = TypeAccountName(accountName)
        }

    init {
        actor = TypeAccountName(accountName)
        permission = TypePermissionName(permissionName)
    }

    fun getPermission(): String {
        return permission.toString()
    }

    fun setPermission(permissionName: String) {
        permission = TypePermissionName(permissionName)
    }

    override fun pack(writer: EosType.Writer) {
        actor.pack(writer)
        permission.pack(writer)
    }
}
