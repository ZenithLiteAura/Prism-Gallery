package com.example.amoledgallery.data

import android.content.Context
import com.example.amoledgallery.ui.features.MediaOps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 私密相册功能已从应用里删除。升级后第一次启动时，把之前搬进 `filesDir/private` 的照片/视频
 * 一次性搬回 `Pictures/Restored`，这样它们不会因为删功能而消失；全部搬完后清掉旧的 PIN 记录。
 *
 * 失败（例如存储不可写）时保留源文件，下次启动再试，绝不删除没搬成功的文件。
 */
object LegacyPrivateMigration {
    private const val PREFS = "prism_private_album"

    suspend fun run(context: Context) = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "private")
        if (!dir.isDirectory) return@withContext
        val files = dir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") }.orEmpty()
        var moved = 0
        for (file in files) {
            val ok = runCatching {
                MediaOps.saveNew(
                    context,
                    file.name,
                    mimeOf(file.name),
                    android.os.Environment.DIRECTORY_PICTURES + "/Restored"
                ) { out -> file.inputStream().use { it.copyTo(out) } } != null
            }.getOrDefault(false)
            if (ok) {
                file.delete()
                moved++
            }
        }
        if (moved > 0 || dir.listFiles().orEmpty().isEmpty()) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        }
    }

    private fun mimeOf(name: String): String = when (name.substringAfterLast('.', "").lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "heic", "heif" -> "image/heic"
        "avif" -> "image/avif"
        "bmp" -> "image/bmp"
        "gif" -> "image/gif"
        "mp4", "m4v" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "webm" -> "video/webm"
        "mov" -> "video/quicktime"
        "3gp" -> "video/3gpp"
        else -> "application/octet-stream"
    }
}