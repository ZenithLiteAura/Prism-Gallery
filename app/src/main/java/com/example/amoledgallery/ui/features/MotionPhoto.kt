package com.example.amoledgallery.ui.features

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.amoledgallery.model.MediaItem
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Motion photos (Google's "Motion"/"MicroVideo" and Samsung's "Motion photo"): a JPEG with a short MP4 appended
 * after the picture. The video part is found by its MP4 header and copied into the cache to be played.
 *
 * Playback: the viewer starts it by itself as soon as the page opens (see MediaDetailScreen) - the clip is drawn
 * over the photo with no seek bar, and the pager keeps handling left/right swipes. This object only finds the
 * offset, extracts the clip and saves it as a normal video.
 */
object MotionPhoto {
    /** Offset of the embedded video by media id, or -1 for "none". */
    private val offsets = ConcurrentHashMap<Long, Long>()

    private fun cacheFile(context: Context, item: MediaItem) = File(context.cacheDir, "motion/${item.id}_${item.dateModified}.mp4")

    /** True once [videoOffset] found a video in this photo (without reading the file). */
    fun isKnownMotion(id: Long): Boolean = (offsets[id] ?: -1L) >= 0L

    /** Where the embedded MP4 starts, or null. Reads the file once; blocking. */
    fun videoOffset(context: Context, item: MediaItem): Long? {
        if (item.isVideo || item.isGif) return null
        val mime = item.mimeType.lowercase()
        if (!mime.contains("jpeg") && !mime.contains("jpg") && !mime.contains("heic") && !mime.contains("heif")) return null
        offsets[item.id]?.let { return it.takeIf { o -> o >= 0 } }
        val found = runCatching { findOffset(context, item.uri) }.getOrNull()
        offsets[item.id] = found ?: -1L
        return found
    }

    private fun findOffset(context: Context, uri: Uri): Long? {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        // Only files that say they are motion photos: XMP markers, or Samsung's trailer.
        val head = String(bytes, 0, minOf(bytes.size, 256 * 1024), Charsets.ISO_8859_1)
        val tail = String(bytes, maxOf(0, bytes.size - 64 * 1024), minOf(bytes.size, 64 * 1024), Charsets.ISO_8859_1)
        val marked = head.contains("MotionPhoto") || head.contains("MicroVideo") || tail.contains("MotionPhoto_Data")
        if (!marked) return null
        // The MP4 starts with a box "....ftyp" whose size field is small; search after the first kilobytes.
        var i = 1024
        while (i < bytes.size - 12) {
            if (bytes[i] == 'f'.code.toByte() && bytes[i + 1] == 't'.code.toByte() && bytes[i + 2] == 'y'.code.toByte() && bytes[i + 3] == 'p'.code.toByte()) {
                val start = i - 4
                val size = ((bytes[start].toInt() and 0xff) shl 24) or ((bytes[start + 1].toInt() and 0xff) shl 16) or
                    ((bytes[start + 2].toInt() and 0xff) shl 8) or (bytes[start + 3].toInt() and 0xff)
                if (size in 8..256) return start.toLong()
            }
            i++
        }
        return null
    }

    /** The embedded video as a file in the cache (made on first use), or null. Blocking. */
    fun extract(context: Context, item: MediaItem): File? {
        val offset = videoOffset(context, item) ?: return null
        val file = cacheFile(context, item)
        if (file.exists() && file.length() > 0) return file
        file.parentFile?.mkdirs()
        // Old extractions are only needed while their photo is looked at.
        file.parentFile?.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 24 * 3_600_000L }?.forEach { it.delete() }
        context.contentResolver.openInputStream(item.uri)?.use { input ->
            input.skip(offset)
            file.outputStream().use { input.copyTo(it) }
        }
        return file.takeIf { it.length() > 0 }
    }

    /** Saves the embedded video to Movies/Motion photos. */
    fun saveVideo(context: Context, item: MediaItem): Uri? {
        val file = extract(context, item) ?: return null
        val base = item.displayName.substringBeforeLast('.').ifBlank { "motion_${item.id}" }
        return MediaOps.saveNew(context, "$base.mp4", "video/mp4", "${Environment.DIRECTORY_MOVIES}/Motion photos") { out ->
            file.inputStream().use { it.copyTo(out) }
        }
    }
}