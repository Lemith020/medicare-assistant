package com.example.medicare_assistant.records.logic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object RecordImageHelper {

    private const val MAX_SIDE = 1600

    fun newCameraTarget(context: Context): Uri {
        val dir = File(context.cacheDir, "camera").apply { mkdirs() }
        val file = File(dir, "record_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    suspend fun importToPending(context: Context, source: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver

            val orientation = resolver.openInputStream(source)?.use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL

            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(source)?.use { BitmapFactory.decodeStream(it, null, bounds) }

            var sample = 1
            while (bounds.outWidth / sample > MAX_SIDE * 2 || bounds.outHeight / sample > MAX_SIDE * 2) sample *= 2

            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            val decoded = resolver.openInputStream(source)?.use { BitmapFactory.decodeStream(it, null, options) }
                ?: return@runCatching null

            val finalBitmap = scaleDown(applyOrientation(decoded, orientation))

            val dir = File(context.cacheDir, "records_pending").apply { mkdirs() }
            dir.listFiles()?.forEach { it.delete() }
            val target = File(dir, "record_${System.currentTimeMillis()}.jpg")
            FileOutputStream(target).use { finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }

            File(context.cacheDir, "camera").listFiles()
                ?.filter { it.name.startsWith("record_") }
                ?.forEach { it.delete() }
            target.absolutePath
        }.getOrNull()
    }

    suspend fun commit(context: Context, pendingPath: String, previousPath: String?): String =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "records").apply { mkdirs() }
            val target = File(dir, "record_${System.currentTimeMillis()}.jpg")
            File(pendingPath).copyTo(target, overwrite = true)
            File(pendingPath).delete()
            previousPath?.let { File(it).delete() }
            target.absolutePath
        }

    suspend fun delete(path: String?) {
        if (path == null) return
        withContext(Dispatchers.IO) { File(path).delete() }
    }

    private fun applyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun scaleDown(bitmap: Bitmap): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= MAX_SIDE) return bitmap
        val ratio = MAX_SIDE.toFloat() / longest
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * ratio).toInt(),
            (bitmap.height * ratio).toInt(),
            true
        )
    }
}