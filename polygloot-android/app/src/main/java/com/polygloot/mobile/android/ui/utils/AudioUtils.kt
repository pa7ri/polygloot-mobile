package com.polygloot.mobile.android.ui.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.polygloot.mobile.android.ui.utils.Consts.Companion.AUDIO_RESPONSE_NAME
import com.polygloot.mobile.android.ui.utils.Consts.Companion.REQUEST_RECORD_AUDIO_PERMISSION
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioUtils {
    companion object {

        fun isRecordAudioPermissionGranted(context: Context) = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        fun requestAudioPermissions(context: Activity) = ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )

        fun decodeBase64Audio(base64Audio: String): ByteArray {
            return Base64.decode(base64Audio, Base64.DEFAULT)
        }

        fun encodeBase64Audio(fileDir: String, fileName: String): String {
            val audioFile = File(fileDir, fileName)
            return Base64.encodeToString(audioFile.readBytes(), Base64.NO_WRAP)
        }

        fun saveAudioToInternalStorage(
            fileDir: String,
            audioBytes: ByteArray,
            fileName: String = AUDIO_RESPONSE_NAME
        ): File {
            val audioFile = File(fileDir, fileName)
            try {
                FileOutputStream(audioFile).use { fos ->
                    fos.write(audioBytes)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return audioFile
        }

    }
}