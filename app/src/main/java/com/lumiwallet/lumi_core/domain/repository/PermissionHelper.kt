package com.lumiwallet.lumi_core.domain.repository

interface PermissionHelper {
    fun <T>checkFilesystemPermission(onGranted: () -> T, onDenied: () -> T): T
    fun checkFilesystemPermission(): Boolean
}