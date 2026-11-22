package com.example.amoledgallery.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.amoledgallery.model.MediaItem

/**
 * 「浏览目录」里自选目录的文件不在系统回收站里，删除就是永久删除。
 * 任何删除入口（查看器、多选工具栏）先把要删的项放到这里，由 MainActivity 弹一次确认，
 * 确认后才调 ViewModel 真正删盘上的文件。
 */
object LocalDeleteRequest {
    var items by mutableStateOf<List<MediaItem>>(emptyList())
        private set

    fun request(targets: List<MediaItem>) {
        if (targets.isEmpty()) return
        items = targets
    }

    fun clear() {
        items = emptyList()
    }
}