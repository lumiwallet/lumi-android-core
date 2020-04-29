package com.lumiwallet.lumi_core.domain.generateRandom

import android.os.Environment
import com.lumiwallet.lumi_core.domain.repository.PermissionHelper
import io.reactivex.rxjava3.core.Completable
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.inject.Inject

class GenerateRandomUseCase @Inject constructor(
    private val permissionHelper: PermissionHelper
) {

    class PermissionDeniedException : IllegalStateException()

    operator fun invoke(length: String): Completable = Completable.create {
        permissionHelper.checkFilesystemPermission(
            onGranted = {
                val bytestreamLength = length.toInt()
                if (bytestreamLength <= 0) throw Exception("wrong length")
                val random = SecureRandom()
                val bytes = ByteArray(bytestreamLength)
                random.nextBytes(bytes)
                val file = File(Environment.getExternalStorageDirectory(), "bytes.pi")
                if (!file.exists()) {
                    file.createNewFile()
                }
                FileOutputStream(file).apply {
                    write(bytes)
                    close()
                }
                it.onComplete()
            },
            onDenied = {
                throw PermissionDeniedException()
            })
    }
}