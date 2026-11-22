package com.example.amoledgallery.model

import android.net.Uri

/**
 * 文件夹选择器里的一项：MediaStore 认识的一个文件夹（bucket）。
 * [relativePath] 是用于过滤的相对路径前缀（以 "/" 结尾），[count] 是其中的照片 + 视频数。
 */
data class LibraryFolder(
    val bucketId: String,
    val name: String,
    val relativePath: String,
    val count: Int,
    /** 在 SD 卡 / U 盘上（卷名不是主卷）。 */
    val isSdCard: Boolean,
    val coverUri: Uri?
)