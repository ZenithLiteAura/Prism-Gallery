package com.example.amoledgallery.data

import android.net.Uri
import com.example.amoledgallery.model.MediaItem
import java.io.File

/**
 * 从磁盘直接扫描「浏览目录」里自选的文件夹，生成 [MediaItem]。
 *
 * 和媒体库不同，这里**不跳过隐藏目录、也不看 .nomedia**：用户既然手动选了，就按选中的来。
 * 扫出来的条目带负 id（路径哈希）和 file:// URI，靠「所有文件访问」权限读。
 *
 * 保护措施：只认照片/视频扩展名，限制单目录条数、递归深度和总数；跳过 Android/data、Android/obb
 * 和媒体库自己的回收站残影（".trashed-*"、".pending-*"）。
 */
object LocalMediaScanner {

    private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp", "heic", "heif", "avif", "bmp", "gif")
    private val VIDEO_EXTENSIONS = setOf("mp4", "mkv", "webm", "m4v", "3gp", "mov", "avi", "ts", "3g2", "mts")

    private const val MAX_PER_DIR = 5_000
    private const val MAX_DEPTH = 12
    private const val MAX_TOTAL = 20_000

    /**
     * [dirs] 自选目录的绝对路径；[skipPaths] 已经由媒体库返回的文件（去重用，避免同一张照片出现两次）。
     */
    fun scan(dirs: Set<String>, skipPaths: Set<String>): List<MediaItem> {
        if (dirs.isEmpty()) return emptyList()
        val result = ArrayList<MediaItem>()
        val visitedDirs = HashSet<String>()
        for (dir in dirs) {
            if (result.size >= MAX_TOTAL) break
            val root = File(dir)
            if (!root.isDirectory) continue
            walk(root, 0, skipPaths, visitedDirs, result)
        }
        return result
    }

    private fun walk(
        dir: File,
        depth: Int,
        skipPaths: Set<String>,
        visitedDirs: MutableSet<String>,
        out: MutableList<MediaItem>
    ) {
        if (depth > MAX_DEPTH || out.size >= MAX_TOTAL) return
        val canonical = runCatching { dir.canonicalPath }.getOrNull() ?: return
        if (!visitedDirs.add(canonical)) return

        val children = runCatching { dir.listFiles() }.getOrNull() ?: return
        var seen = 0
        for (child in children.sortedBy { it.name.lowercase() }) {
            if (seen >= MAX_PER_DIR || out.size >= MAX_TOTAL) break
            seen++
            if (child.isDirectory) {
                // Android/data 与 Android/obb 连“所有文件访问”也读不到，列出来只会是空目录。
                val path = child.path
                if (path.endsWith("/Android/data") || path.endsWith("/Android/obb")) continue
                walk(child, depth + 1, skipPaths, visitedDirs, out)
                continue
            }
            val item = toItem(child, skipPaths) ?: continue
            out.add(item)
        }
    }

    private fun toItem(file: File, skipPaths: Set<String>): MediaItem? {
        val name = file.name
        // 媒体库回收站的残影，不是用户的照片。
        if (name.startsWith(".trashed-") || name.startsWith(".pending-")) return null
        val extension = name.substringAfterLast('.', "").lowercase()
        val isVideo = extension in VIDEO_EXTENSIONS
        if (!isVideo && extension !in IMAGE_EXTENSIONS) return null
        val absolutePath = file.absolutePath
        if (absolutePath in skipPaths) return null

        val parent = file.parentFile ?: return null
        val lastModified = file.lastModified().coerceAtLeast(0L) / 1000L
        return MediaItem(
            id = idFor(absolutePath),
            uri = Uri.fromFile(file),
            isVideo = isVideo,
            dateAdded = lastModified,
            durationMs = 0L,
            displayName = name,
            bucketId = "local:${parent.absolutePath}",
            bucketDisplayName = parent.name,
            relativePath = "",
            dateModified = lastModified,
            isTrashed = false,
            mimeType = mimeOf(extension, isVideo),
            dataPath = absolutePath,
            dateTaken = 0L,
            width = 0,
            height = 0,
            orientation = 0,
            size = file.length()
        )
    }

    private fun mimeOf(extension: String, isVideo: Boolean): String =
        if (isVideo) {
            when (extension) {
                "mp4", "m4v" -> "video/mp4"
                "mkv" -> "video/x-matroska"
                "webm" -> "video/webm"
                "mov" -> "video/quicktime"
                "avi" -> "video/x-msvideo"
                "3gp", "3g2" -> "video/3gpp"
                "ts", "mts" -> "video/mp2t"
                else -> "video/*"
            }
        } else {
            when (extension) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "webp" -> "image/webp"
                "heic", "heif" -> "image/heic"
                "avif" -> "image/avif"
                "bmp" -> "image/bmp"
                "gif" -> "image/gif"
                else -> "image/*"
            }
        }

    /** 稳定的负 id：路径的 64 位 FNV-1a，保证收藏等按 id 存的东西重启后仍然对得上。 */
    fun idFor(path: String): Long {
        var hash = -3750763034362895579L // FNV-1a 64 offset basis
        for (byte in path.toByteArray(Charsets.UTF_8)) {
            hash = hash xor (byte.toLong() and 0xffL)
            hash *= 1099511628211L
        }
        return if (hash > 0L) -hash else if (hash == 0L) -1L else hash
    }
}