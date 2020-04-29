package com.lumiwallet.lumi_core.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.lumiwallet.lumi_core.domain.repository.PermissionHelper

class PermissionHelper(
    private val context: Context
) : PermissionHelper {

    override fun <T> checkFilesystemPermission(onGranted: () -> T, onDenied: () -> T): T =
        if (checkFilesystemPermission()) onGranted() else onDenied()

    override fun checkFilesystemPermission(): Boolean {
        return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}