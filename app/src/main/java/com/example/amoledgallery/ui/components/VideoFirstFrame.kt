package com.example.amoledgallery.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.LruCache
import com.example.amoledgallery.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 视频第一帧的小缓存：查看器在视频真正出画之前用它垫底。
 *
 * 网格和系统缩略图给的是「代表帧」（经常是中间某帧），拿它当预览会和播放的第一帧对不上，
 * 看起来像闪了一下别的画面；这里单独取第 0 帧，缓存几张就够翻页用。
 */
internal object VideoFirstFrameCache {
    private val cache = LruCache<String, Bitmap>(6)
    private val loading = HashSet<String>()

    private fun keyOf(item: MediaItem): String = item.uri.toString() + "|" + item.dateModified

    fun peek(item: MediaItem): Bitmap? = cache.get(keyOf(item))

    suspend fun load(context: Context, item: MediaItem): Bitmap? = withContext(Dispatchers.IO) {
        // 动态照片（durationMs = 0）也走这里：它播放的是一段视频，同样要拿视频的第 0 帧，
        // 否则预览和播放首帧对不上，翻到它/打开它会闪一下别的画面。
        val key = keyOf(item)
        cache.get(key)?.let { return@withContext it }
        synchronized(loading) {
            if (!loading.add(key)) return@withContext cache.get(key)
        }
        try {
            val bmp = runCatching {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, item.uri)
                    retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                } finally {
                    retriever.release()
                }
            }.getOrNull() ?: return@withContext null
            val maxW = 640
            val scaled = if (bmp.width > maxW) {
                val h = (bmp.height.toFloat() * maxW / bmp.width).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(bmp, maxW, h, true).also { if (it !== bmp) bmp.recycle() }
            } else bmp
            cache.put(key, scaled)
            scaled
        } finally {
            synchronized(loading) { loading.remove(key) }
        }
    }
}
