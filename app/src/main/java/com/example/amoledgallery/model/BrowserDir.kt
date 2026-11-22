package com.example.amoledgallery.model

/**
 * 「浏览目录」里的一项：一个真实目录（不是媒体库里的相册）。
 *
 * @property path 绝对路径
 * @property isRoot 存储卷的根（内部存储、SD 卡、U 盘）
 * @property removable 可移动卷（SD 卡 / U 盘）
 * @property mediaCount 该目录下（不递归）的照片 + 视频数
 */
data class BrowserDir(
    val name: String,
    val path: String,
    val isRoot: Boolean = false,
    val removable: Boolean = false,
    val mediaCount: Int = 0
)