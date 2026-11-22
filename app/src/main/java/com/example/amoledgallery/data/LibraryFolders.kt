package com.example.amoledgallery.data

import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 图库到底读取哪些文件夹。
 *
 * 默认「只读取所选文件夹」且一个都没选：图库/相册/搜索/重复/清理/地点默认全是空的，
 * 用户要自己在「设置 → 读取的文件夹」里挑。这样系统相机的 DCIM、下载的 Download、
 * 各种截图目录都不会自动混进来。
 *
 * 过滤条件由 [sqlSelection] 拼成 MediaStore 的 RELATIVE_PATH 前缀匹配，交给
 * [MediaRepository] 的每个媒体查询；勾选变化时 [revision] 自增，ViewModel 监听它重载。
 */
object LibraryFolders {
    private const val PREFS_NAME = "amoled_gallery_folders"
    private const val KEY_ONLY_SELECTED = "only_selected"
    private const val KEY_PATHS = "selected_paths"
    private const val KEY_LOCAL_PATHS = "local_dirs"

    /** 只读取勾选的文件夹（默认开）。关掉就是读取全部文件夹。 */
    var onlySelected by mutableStateOf(true)
        private set

    /** 勾选的文件夹，归一化成 "Pictures/Camera/" 这样的相对路径前缀（媒体库口径）。 */
    var paths by mutableStateOf<Set<String>>(emptySet())
        private set

    /** 「浏览目录」里自选的目录，绝对路径（直接读磁盘，含隐藏目录，不经过媒体库）。 */
    var localPaths by mutableStateOf<Set<String>>(emptySet())
        private set

    /** 每次改动 +1；[com.example.amoledgallery.ui.viewmodel.GalleryViewModel] 监听它重载媒体。 */
    var revision by mutableStateOf(0)
        private set

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        onlySelected = prefs.getBoolean(KEY_ONLY_SELECTED, true)
        paths = (prefs.getStringSet(KEY_PATHS, emptySet()) ?: emptySet())
            .mapNotNull { normalize(it) }
            .toSet()
        localPaths = (prefs.getStringSet(KEY_LOCAL_PATHS, emptySet()) ?: emptySet())
            .mapNotNull { normalizeLocalDir(it) }
            .toSet()
        revision++
    }

    fun setOnlySelected(value: Boolean, context: Context) {
        if (onlySelected == value) return
        onlySelected = value
        persist(context)
        revision++
    }

    fun toggle(path: String, context: Context) {
        val key = normalize(path) ?: return
        paths = if (key in paths) paths - key else paths + key
        persist(context)
        revision++
    }

    fun selectAll(all: Collection<String>, context: Context) {
        paths = all.mapNotNull { normalize(it) }.toSet()
        persist(context)
        revision++
    }

    fun clear(context: Context) {
        paths = emptySet()
        localPaths = emptySet()
        persist(context)
        revision++
    }

    /** 「浏览目录」：把一个绝对目录加入/移出直接读盘的列表。 */
    fun toggleLocalDir(path: String, context: Context) {
        val key = normalizeLocalDir(path) ?: return
        localPaths = if (key in localPaths) localPaths - key else localPaths + key
        persist(context)
        revision++
    }

    fun removeLocalDir(path: String, context: Context) {
        val key = normalizeLocalDir(path) ?: return
        if (key !in localPaths) return
        localPaths = localPaths - key
        persist(context)
        revision++
    }

    fun isLocalSelected(path: String): Boolean {
        val key = normalizeLocalDir(path) ?: return false
        return key in localPaths
    }

    /** 两种模式合计选了几个文件夹（给设置入口的摘要）。 */
    fun count(): Int = paths.size + localPaths.size

    /** 有没有任何选择：图库空状态用它判断是“没选文件夹”还是“真的没有媒体”。 */
    fun hasAnySelection(): Boolean = paths.isNotEmpty() || localPaths.isNotEmpty()

    /**
     * MediaStore 查询条件。
     * - null → 不过滤（读取全部）
     * - "0 = 1" → 一个文件夹都没勾，什么都不显示
     * - 否则 RELATIVE_PATH LIKE 'p%' OR ...
     */
    fun sqlSelection(): Pair<String?, Array<String>?> {
        if (!onlySelected) return null to null
        val prefixes = paths.filter { it.isNotBlank() }.sorted()
        if (prefixes.isEmpty()) return "0 = 1" to emptyArray()
        val clause = prefixes.joinToString(" OR ") { "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?" }
        return "($clause)" to prefixes.map { "$it%" }.toTypedArray()
    }

    /** "Pictures/Camera" → "Pictures/Camera/"；空、"/" 或非法输入返回 null。 */
    fun normalize(path: String?): String? {
        val trimmed = path?.trim()?.replace('\\', '/')?.trim('/') ?: return null
        if (trimmed.isEmpty() || trimmed.contains("..")) return null
        return "$trimmed/"
    }

    /** 绝对目录 → 去掉结尾 "/" 的规范形式；必须是以 "/" 开头的绝对路径。 */
    fun normalizeLocalDir(path: String?): String? {
        val trimmed = path?.trim()?.replace('\\', '/')?.trimEnd('/') ?: return null
        if (trimmed.isEmpty() || !trimmed.startsWith("/") || trimmed.contains("..")) return null
        return trimmed
    }

    private fun persist(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ONLY_SELECTED, onlySelected)
            .putStringSet(KEY_PATHS, paths)
            .putStringSet(KEY_LOCAL_PATHS, localPaths)
            .apply()
    }
}