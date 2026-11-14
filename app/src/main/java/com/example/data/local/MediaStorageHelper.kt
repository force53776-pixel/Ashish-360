package com.example.data.local

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MediaStorageHelper(private val context: Context) {

    private val mediaDir: File
        get() = File(context.filesDir, "my_life_media").apply { if (!exists()) mkdirs() }

    private val thumbDir: File
        get() = File(context.cacheDir, "video_thumbs").apply { if (!exists()) mkdirs() }

    suspend fun copyUriToInternalStorage(uri: Uri, defaultExt: String = "jpg"): String =
        withContext(Dispatchers.IO) {
            val mimeType = context.contentResolver.getType(uri)
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: defaultExt
            val destFile = File(mediaDir, "media_${UUID.randomUUID()}.$ext")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: throw IllegalStateException("Could not read selected media")
            destFile.absolutePath
        }

    suspend fun getOrCreateVideoThumbnail(videoPath: String): String? =
        withContext(Dispatchers.IO) {
            if (videoPath.isBlank()) return@withContext null
            val videoFile = File(videoPath)
            if (!videoFile.exists()) return@withContext null

            val thumbFile = File(thumbDir, "${videoFile.nameWithoutExtension}.jpg")
            if (thumbFile.exists()) return@withContext thumbFile.absolutePath

            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(videoPath)
                val frame = retriever.getFrameAtTime(500_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    ?: retriever.frameAtTime
                if (frame != null) {
                    FileOutputStream(thumbFile).use { out ->
                        frame.compress(Bitmap.CompressFormat.JPEG, 82, out)
                    }
                    thumbFile.absolutePath
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) {
                }
            }
        }

    fun deleteMediaFile(path: String) {
        if (path.isBlank()) return
        try {
            val file = File(path)
            if (file.exists() && file.absolutePath.startsWith(mediaDir.absolutePath)) {
                file.delete()
            }
        } catch (_: Exception) {
        }
    }
}
